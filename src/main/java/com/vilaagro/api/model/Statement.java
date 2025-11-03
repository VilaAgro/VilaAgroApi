// VilaAgroApi/src/main/java/com/vilaagro/api/model/Statement.java
package com.vilaagro.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um Comunicado (Statement)
 * Mapeia a tabela 'statement' (RF-D.5)
 */
@Entity
@Table(name = "statement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin; // O Admin que criou o comunicado

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column
    private String stereotype; // Categoria, ex: "Urgente", "Informativo"

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}