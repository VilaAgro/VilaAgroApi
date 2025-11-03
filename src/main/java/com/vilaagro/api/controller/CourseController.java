package com.vilaagro.api.controller;

import com.vilaagro.api.dto.CourseCreateDTO;
import com.vilaagro.api.dto.CourseResponseDTO;
import com.vilaagro.api.dto.CourseUpdateDTO;
import com.vilaagro.api.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gerenciamento de cursos
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Cria um novo curso
     */
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseCreateDTO createDTO) {
        CourseResponseDTO createdCourse = courseService.createCourse(createDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * Lista todos os cursos
     */
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Lista cursos futuros
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<CourseResponseDTO>> getUpcomingCourses() {
        List<CourseResponseDTO> courses = courseService.getUpcomingCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Busca um curso por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable UUID id) {
        CourseResponseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Busca cursos por título
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> searchCoursesByTitle(@RequestParam String title) {
        List<CourseResponseDTO> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    /**
     * Atualiza um curso
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseUpdateDTO updateDTO) {
        CourseResponseDTO updatedCourse = courseService.updateCourse(id, updateDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Deleta um curso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<CourseResponseDTO> enrollInCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        CourseResponseDTO course = courseService.enrollInCourse(id, currentUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    /**
     * Comerciante: Cancela inscrição em um curso (RF-C.4)
     */
    @DeleteMapping("/{id}/enroll")
    public ResponseEntity<Void> cancelEnrollment(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        courseService.cancelEnrollment(id, currentUser.getUser());
        return ResponseEntity.noContent().build();
    }

    /**
     * Comerciante: Lista os cursos em que está inscrito (RF-C.4.3)
     */
    @GetMapping("/enrolled/me")
    public ResponseEntity<List<CourseResponseDTO>> getMyEnrolledCourses(
            @AuthenticationPrincipal CustomUserDetailsService.CustomUserPrincipal currentUser
    ) {
        List<CourseResponseDTO> courses = courseService.getMyEnrolledCourses(currentUser.getUser());
        return ResponseEntity.ok(courses);
    }

    /**
     * Admin: Lista os usuários inscritos em um curso (RF-D.5.4)
     */
    @GetMapping("/{id}/enrolled-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getEnrolledUsersForCourse(@PathVariable UUID id) {
        List<UserResponseDTO> users = courseService.getEnrolledUsersForCourse(id);
        return ResponseEntity.ok(users);
    }
}
