package com.acme.encuestas.repository;

import com.acme.encuestas.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> { }
