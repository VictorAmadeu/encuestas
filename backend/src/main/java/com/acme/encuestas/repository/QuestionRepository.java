package com.acme.encuestas.repository;

import com.acme.encuestas.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> { }
