package com.kerp.modules.notification.service;

import com.kerp.modules.notification.dto.NotificationCreateRequest;
import com.kerp.modules.notification.entity.NotificationMessage;

import java.util.List;

public interface NotificationService {

    NotificationMessage createNotification(NotificationCreateRequest request);

    List<NotificationMessage> listNotifications();

    void markAllAsRead();
}
