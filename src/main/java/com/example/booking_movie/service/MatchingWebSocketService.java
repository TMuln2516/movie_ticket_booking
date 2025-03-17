package com.example.booking_movie.service;

import com.example.booking_movie.dto.response.MatchingInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MatchingWebSocketService {
//    SimpMessagingTemplate simpMessagingTemplate;
//    SimpUserRegistry simpUserRegistry;
//
//    public void notifyUserMatched(String userId, MatchingInfo matchingInfo) {
//        String destination = "/queue/matching"; // ✅ Dùng /queue thay vì /topic
//
//        log.info("📢 Gửi tin nhắn đến WebSocket: {}", destination);
//
//        simpMessagingTemplate.convertAndSendToUser(userId, destination, matchingInfo);
//
//        log.info("✅ Tin nhắn đã được gửi thành công đến user: {}", userId);
//    }
}
