package com.example.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageDto {
    private Long conversationId;

    @NotBlank
    private String content;

}
