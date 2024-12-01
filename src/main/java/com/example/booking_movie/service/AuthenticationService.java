package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.*;
import com.example.booking_movie.dto.response.AuthenticationResponse;
import com.example.booking_movie.dto.response.IntrospectResponse;
import com.example.booking_movie.entity.InvalidatedToken;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.InvalidatedTokenRepository;
import com.example.booking_movie.repository.RoleRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.repository.client.OutboundClient;
import com.example.booking_movie.repository.client.OutboundUserClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${myapp.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${outbound.client_id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.client_secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.redirect_uri}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("${outbound.grant_type}")
    protected String GRANT_TYPE;

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundClient outboundClient;
    OutboundUserClient outboundUserClient;
    RoleRepository roleRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new MyException(ErrorCode.PASSWORD_OR_USERNAME_INCORRECT));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

//        check ban
        if (!user.getStatus()) {
            throw new MyException(ErrorCode.ACCOUNT_BANNED);
        }

        if (!authenticated) {
            throw new MyException(ErrorCode.PASSWORD_OR_USERNAME_INCORRECT);
        }

        var token = generateToken(user);

//        log
//        log.info("Token: " + token);

        return AuthenticationResponse.builder()
                .token(token)
                .build();

    }

    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        var signToken = verifyToken(logoutRequest.getToken());
        String jti = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new MyException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new MyException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new MyException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String generateToken(User user) {
        // Create Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // Create Payload
        // Create Claims => Data in Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                // Domain name
                .issuer("localhost:8080")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        // Create Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Create Token (header, payload)
        JWSObject jwsObject = new JWSObject(header, payload);

        // SIGN token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;

        try {
            verifyToken(token);
        } catch (MyException exception) {
            isValid = false;
        }

        SignedJWT signedJWT = verifyToken(request.getToken());
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        String role = claimsSet.getStringClaim("scope");

        return IntrospectResponse.builder()
                .valid(isValid)
                .role(extractUserRole(role))
                .build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner;
        stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }
        return stringJoiner.toString();
    }

    private String extractUserRole(String role) {
        if (role != null && role.startsWith("ROLE_")) {
            return role.substring(5);
        }
        return role;
    }

//    outbound service
    public AuthenticationResponse outboundAuthenticate(String code) {
        var response = outboundClient.exchangeToken(ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                .build());

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
//        log.info("User Info: {}", userInfo);

        //       set roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(DefinedRole.USER_ROLE).orElseThrow());

        var user = userRepository.findByUsername(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                                .username(userInfo.getEmail())
                                .firstName(userInfo.getGivenName())
                                .lastName(userInfo.getFamilyName())
                                .email(userInfo.getEmail())
                                .status(true)
                                .roles(roles)
                        .build()));

//        tạo token mới để đăng nhập hệ thống
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
