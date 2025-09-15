package com.acme.encuestas.repository;

import com.acme.encuestas.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    List<Survey> findByOwnerId(UUID ownerId);
    List<Survey> findByStatus(String status);
}
