package com.kerp.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 128, message = "Title is too long")
        String title,
        @NotBlank(message = "Content is required")
        @Size(max = 500, message = "Content is too long")
        String content,
        @NotBlank(message = "Target type is required")
        @Size(max = 32, message = "Target type is too long")
        String targetType,
        @NotBlank(message = "Target value is required")
        @Size(max = 128, message = "Target value is too long")
        String targetValue
) {
}
