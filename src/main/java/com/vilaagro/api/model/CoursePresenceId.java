package com.vilaagro.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePresenceId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "course_id")
    private UUID courseId;
}