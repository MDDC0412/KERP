package com.kerp.modules.notification.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.notification.dto.NotificationCreateRequest;
import com.kerp.modules.notification.entity.NotificationMessage;
import com.kerp.modules.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationMessage>> listNotifications() {
        return ApiResponse.success(notificationService.listNotifications());
    }

    @PostMapping
    public ApiResponse<NotificationMessage> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        return ApiResponse.success(notificationService.createNotification(request));
    }

    @PostMapping("/mark-all-read")
    public ApiResponse<Void> markAllRead() {
        notificationService.markAllAsRead();
        return ApiResponse.successMessage("所有通知已标记为已读");
    }
}
