package com.acme.encuestas.document;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AnswerDocument {
    private String questionId;
    private String type;            // "OPEN" | "MULTI"
    private List<String> optionIds; // para MULTI
    private String textAnswer;      // para OPEN
}
