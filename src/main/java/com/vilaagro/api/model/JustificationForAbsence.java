// VilaAgroApi/src/main/java/com/vilaagro/api/model/JustificationForAbsence.java
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
 * Entidade que representa a justificativa para uma ausência
 * (RF-C.3.2)
 */
@Entity
@Table(name = "justification_for_absence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JustificationForAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absence_id", nullable = false, unique = true)
    private Absence absence;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // Caminho do arquivo anexado (se houver)
    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "approved_by_admin_id")
    private UUID approvedByAdminId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}