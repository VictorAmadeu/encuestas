package com.acme.encuestas.repository;

import com.acme.encuestas.document.ResponseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ResponseRepository extends MongoRepository<ResponseDocument, String> {

    boolean existsBySurveyIdAndRespondentId(String surveyId, String respondentId);

    List<ResponseDocument> findBySurveyId(String surveyId);

    // Útil si quieres bloquear el borrado de encuestas con respuestas
    boolean existsBySurveyId(String surveyId);
}
