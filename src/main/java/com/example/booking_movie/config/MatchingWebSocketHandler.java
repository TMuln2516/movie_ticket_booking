package com.example.booking_movie.config;

import com.example.booking_movie.dto.response.MatchingInfo;
import com.example.booking_movie.entity.Notification;
import com.example.booking_movie.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchingWebSocketHandler extends TextWebSocketHandler {
    // Lưu WebSocket session theo userId (String)
    static ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    NotificationRepository notificationRepository;

    @Override // hàm xử lý khi kết nối đến websocket
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);

//            kiểm tra xem có thông báo nào chưa gửi không
            notificationRepository.findByUserIdAndIsReadFalse(userId)
                    .forEach(notification -> {
                        try {
                            Object result = notification.getData() != null
                                    ? new ObjectMapper().readValue(notification.getData(), Object.class)
                                    : null;

//                            gửi thông báo
                            notifyUser(userId, "Ghép đôi thành công", result);

//                            cập nhật trạng thái đã đọc
                            notification.setIsRead(true);
                            notificationRepository.save(notification);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Override // hàm xử lý khi ngắt kết nối websocket
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }


//    hàm gửi thông báo đến user
    public void notifyUser(String userId, String message, Object result) throws JsonProcessingException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonMessage = objectMapper.writeValueAsString(Map.of(
                        "message", message,
                        "result", result != null ? result : new HashMap<>()
                ));
                session.sendMessage(new TextMessage(jsonMessage));
                System.out.println("📩 Đã gửi JSON đến " + userId + ": " + jsonMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Không tìm thấy kết nối WebSocket cho user: " + userId);

//            lưu lại thông báo vào db
            Notification newNotification = Notification.builder()
                    .userId(userId)
                    .message(message)
                    .data(result != null ? new ObjectMapper().writeValueAsString(result) : null)
                    .isRead(false)
                    .build();
            notificationRepository.save(newNotification);
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