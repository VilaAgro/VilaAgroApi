package com.vilaagro.api.repository;

import com.vilaagro.api.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositório para operações com a entidade Course
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Busca cursos por período
     */
    List<Course> findByDatetimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca cursos futuros ordenados por data
     */
    @Query("SELECT c FROM Course c WHERE c.datetime > :now ORDER BY c.datetime ASC")
    List<Course> findUpcomingCourses(@Param("now") LocalDateTime now);

    /**
     * Busca cursos por endereço
     */
    List<Course> findByAddressId(UUID addressId);

    /**
     * Busca cursos por título (busca parcial, case insensitive)
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Course> findByTitleContainingIgnoreCase(@Param("title") String title);
}
