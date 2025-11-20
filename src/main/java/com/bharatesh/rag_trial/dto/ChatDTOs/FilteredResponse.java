package com.bharatesh.rag_trial.dto.ChatDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredResponse {
    String originalText;
    String anonymizedText;
}
