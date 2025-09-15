package com.acme.encuestas.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Document(collection = "responses")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseDocument {

    @Id
    private String id;              // UUID como String

    private String surveyId;        // UUID de Survey como String
    private String respondentId;    // UUID de User como String

    private Instant submittedAt;

    private List<AnswerDocument> answers;
    private MetaData meta;
}
