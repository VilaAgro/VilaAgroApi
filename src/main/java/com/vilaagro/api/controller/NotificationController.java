package com.vilaagro.api.controller;

import com.vilaagro.api.dto.NotificationResponseDTO;
import com.vilaagro.api.service.CustomUserDetailsService;
import com.vilaagro.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para notificações do usuário
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lista todas notificações - Público
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications() {
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Retorna contagem de notificações não lidas - Público
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Long count = notificationService.getTotalUnreadCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Marca notificação como lida - Público
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca todas notificações como lidas - Público
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }
}
