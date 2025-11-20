package com.bharatesh.rag_trial.dto.ChatDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationalRequest {

    private String codeSnippet;
    private String query;
}