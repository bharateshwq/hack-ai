package com.bharatesh.rag_trial.dto.ChatDTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocsGenRequest {

    private String language;
    private String fileName;
    private String source;   // always "vscode" based on your example
    private String code;
    private String width;
}
