package com.acme.encuestas.document;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MetaData {
    private String ip;
    private String ua;
}
