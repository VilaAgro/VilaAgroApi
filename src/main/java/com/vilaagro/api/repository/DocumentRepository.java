package com.vilaagro.api.repository;

import com.vilaagro.api.model.Document;
import com.vilaagro.api.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByUserId(UUID userId);
    Optional<Document> findByUserIdAndType(UUID userId, DocumentType type);
}
