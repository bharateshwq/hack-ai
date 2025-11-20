package com.bharatesh.rag_trial.Models.ChatModels;


import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @Table(name = "custom_en/tities")
// @Entity
@NoArgsConstructor
public class CustomEntity {
    private String title;
    private List<String> denyList;
    private RegexPatternTypeFilter denyListRegex;

   // Constructor for denyList
    public CustomEntity(String title, List<String> denyList) {
        if (denyList == null || denyList.isEmpty()) {
            throw new IllegalArgumentException("denyList cannot be null or empty");
        }
        this.title = title;
        this.denyList = denyList;
        this.denyListRegex = null;
    }

    // Constructor for denyListRegex
    public CustomEntity(String title, RegexPatternTypeFilter denyListRegex) {
        if (denyListRegex == null) {
            throw new IllegalArgumentException("denyListRegex cannot be null");
        }
        this.title = title;
        this.denyList = null;
        this.denyListRegex = denyListRegex;
    }

}