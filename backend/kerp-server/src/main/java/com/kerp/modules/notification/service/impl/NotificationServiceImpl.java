package com.kerp.modules.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kerp.modules.notification.dto.NotificationCreateRequest;
import com.kerp.modules.notification.entity.NotificationMessage;
import com.kerp.modules.notification.mapper.NotificationMessageMapper;
import com.kerp.modules.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMessageMapper notificationMessageMapper;

    public NotificationServiceImpl(NotificationMessageMapper notificationMessageMapper) {
        this.notificationMessageMapper = notificationMessageMapper;
    }

    @Override
    public NotificationMessage createNotification(NotificationCreateRequest request) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(request.title());
        notificationMessage.setContent(request.content());
        notificationMessage.setTargetType(request.targetType());
        notificationMessage.setTargetValue(request.targetValue());
        notificationMessage.setChannel("IN_APP");
        notificationMessage.setReadStatus(0);
        notificationMessageMapper.insert(notificationMessage);
        return notificationMessage;
    }

    @Override
    public List<NotificationMessage> listNotifications() {
        return notificationMessageMapper.selectList(new LambdaQueryWrapper<NotificationMessage>().orderByDesc(NotificationMessage::getId));
    }

    @Override
    public void markAllAsRead() {
        notificationMessageMapper.update(
                null,
                new LambdaUpdateWrapper<NotificationMessage>()
                        .set(NotificationMessage::getReadStatus, 1)
                        .eq(NotificationMessage::getReadStatus, 0)
        );
    }
}
