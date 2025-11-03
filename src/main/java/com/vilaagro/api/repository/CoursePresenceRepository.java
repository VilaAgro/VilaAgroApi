package com.vilaagro.api.repository;

import com.vilaagro.api.model.CoursePresence;
import com.vilaagro.api.model.CoursePresenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoursePresenceRepository extends JpaRepository<CoursePresence, CoursePresenceId> {

    List<CoursePresence> findByUserId(UUID userId);

    List<CoursePresence> findByCourseId(UUID courseId);
}