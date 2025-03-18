package com.example.booking_movie.config;

import com.example.booking_movie.dto.response.MatchingInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchingWebSocketHandler extends TextWebSocketHandler {
    // Lưu WebSocket session theo userId (String)
    static ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    // Gửi thông báo đến một user cụ thể
//    public void notifyUserMatched(String userId, String message) {
//        WebSocketSession session = userSessions.get(userId);
//        if (session != null && session.isOpen()) {
//            try {
//                session.sendMessage(new TextMessage(message));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void notifyUser(String userId, String message, MatchingInfo matchingInfo) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonMessage = objectMapper.writeValueAsString(Map.of(
                        "message", message,
                        "matchingInfo", matchingInfo != null ? matchingInfo : new HashMap<>()
                ));
                session.sendMessage(new TextMessage(jsonMessage));
                System.out.println("📩 Đã gửi JSON đến " + userId + ": " + jsonMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Không tìm thấy kết nối WebSocket cho user: " + userId);
            System.out.println("📌 Danh sách userSessions hiện tại: " + userSessions.keySet());
        }
    }


    // Lấy userId từ query params khi kết nối WebSocket
    private String extractUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery(); // Lấy phần query string từ URL
            System.out.println("🔍 Query WebSocket: " + query);
            if (query != null && query.contains("userId=")) {
                return query.split("userId=")[1].split("&")[0]; // Cắt userId từ query
            }
        }
        return null;
    }
}