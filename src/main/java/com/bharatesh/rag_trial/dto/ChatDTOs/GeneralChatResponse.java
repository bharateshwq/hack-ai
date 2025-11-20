package com.bharatesh.rag_trial.dto.ChatDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralChatResponse {

    private String aiResponse;

    private boolean responseFromContext;

    private String sanitizedResponse;
}