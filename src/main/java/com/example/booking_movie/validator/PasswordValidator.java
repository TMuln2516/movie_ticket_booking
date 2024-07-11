package com.example.booking_movie.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class PasswordValidator implements ConstraintValidator<PasswordConstrain, String> {
    @Override
    public void initialize(PasswordConstrain constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        Properties props = new Properties();
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream("passay.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageResolver resolver = new PropertiesMessageResolver(props);

        org.passay.PasswordValidator passayPasswordValidator = new org.passay.PasswordValidator(resolver, List.of(
                new LengthRule(8, 255),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));

        RuleResult result = passayPasswordValidator.validate(new PasswordData(password));
        if (result.isValid()) {

            return true;

        }

        List<String> messages = passayPasswordValidator.getMessages(result);

        String messageTemplate = String.join(",", messages);

        constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)

                .addConstraintViolation()

                .disableDefaultConstraintViolation();

        return false;

    }
}
