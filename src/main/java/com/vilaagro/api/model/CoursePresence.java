// VilaAgroApi/src/main/java/com/vilaagro/api/model/CoursePresence.java
package com.vilaagro.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que mapeia a tabela de inscrição (presença) em cursos.
 * (RF-C.4)
 */
@Entity
@Table(name = "course_presence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePresence {

    @EmbeddedId
    private CoursePresenceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Mapeia a parte 'userId' da chave composta
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId") // Mapeia a parte 'courseId' da chave composta
    @JoinColumn(name = "course_id")
    private Course course;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}