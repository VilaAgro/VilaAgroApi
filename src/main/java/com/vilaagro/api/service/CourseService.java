package com.vilaagro.api.service;

import com.vilaagro.api.dto.CourseCreateDTO;
import com.vilaagro.api.dto.CourseResponseDTO;
import com.vilaagro.api.dto.CourseUpdateDTO;
import com.vilaagro.api.exception.ResourceNotFoundException;
import com.vilaagro.api.model.Address;
import com.vilaagro.api.model.Course;
import com.vilaagro.api.repository.AddressRepository;
import com.vilaagro.api.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vilaagro.api.model.CoursePresence;
import com.vilaagro.api.model.CoursePresenceId;
import com.vilaagro.api.model.User;
import com.vilaagro.api.model.UserType;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.repository.CoursePresenceRepository;
import com.vilaagro.api.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de cursos
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final AddressRepository addressRepository;

    private final CoursePresenceRepository coursePresenceRepository;
    private final UserRepository userRepository;
    private final UserService userService; // Para converter User -> DTO

    /**
     * Cria um novo curso
     */
    public CourseResponseDTO createCourse(CourseCreateDTO createDTO) {
        Course.CourseBuilder courseBuilder = Course.builder()
                .title(createDTO.getTitle())
                .description(createDTO.getDescription())
                .datetime(createDTO.getDatetime());

        // Se foi fornecido um addressId, busca e define o relacionamento
        if (createDTO.getAddressId() != null) {
            Address address = addressRepository.findById(createDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", createDTO.getAddressId()));
            courseBuilder.address(address);
        }

        Course course = courseBuilder.build();
        Course savedCourse = courseRepository.save(course);
        return convertToResponseDTO(savedCourse);
    }

    /**
     * Lista todos os cursos
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista cursos futuros
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getUpcomingCourses() {
        return courseRepository.findUpcomingCourses(LocalDateTime.now())
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um curso por ID
     */
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
        return convertToResponseDTO(course);
    }

    /**
     * Busca cursos por título
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza um curso
     */
    public CourseResponseDTO updateCourse(UUID id, CourseUpdateDTO updateDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        // Atualiza os campos não nulos
        if (updateDTO.getTitle() != null) {
            course.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            course.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getDatetime() != null) {
            course.setDatetime(updateDTO.getDatetime());
        }
        if (updateDTO.getAddressId() != null) {
            Address address = addressRepository.findById(updateDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço", "id", updateDTO.getAddressId()));
            course.setAddress(address);
        }

        Course updatedCourse = courseRepository.save(course);
        return convertToResponseDTO(updatedCourse);
    }

    /**
     * Deleta um curso
     */
    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso", "id", id);
        }
        courseRepository.deleteById(id);
    }

    /**
     * Converte Course para CourseResponseDTO
     */
    private CourseResponseDTO convertToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .addressId(course.getAddress() != null ? course.getAddress().getId() : null)
                .title(course.getTitle())
                .description(course.getDescription())
                .datetime(course.getDatetime())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    /**
     * Inscreve o usuário logado em um curso (RF-C.4.2)
     */
    public CourseResponseDTO enrollInCourse(UUID courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", courseId));

        // Regra de Negócio: Admins não podem se inscrever em cursos
        if (currentUser.getType() == UserType.ADMIN) {
            throw new AccessDeniedException("Administradores não podem se inscrever em cursos.");
        }

        // RN-C.4.1: O use case menciona limite de vagas,
        // mas o schema do banco (Course, db.sql) não possui um campo 'vagas'.
        // Portanto, pulamos a verificação de limite de vagas.

        CoursePresenceId enrollmentId = new CoursePresenceId(currentUser.getId(), courseId);
        if (coursePresenceRepository.existsById(enrollmentId)) {
            throw new IllegalStateException("Você já está inscrito neste curso.");
        }

        CoursePresence enrollment = CoursePresence.builder()
                .id(enrollmentId)
                .user(currentUser)
                .course(course)
                .build();

        coursePresenceRepository.save(enrollment);
        return convertToResponseDTO(course);
    }

    /**
     * Cancela a inscrição do usuário logado em um curso (RF-C.4)
     */
    public void cancelEnrollment(UUID courseId, User currentUser) {
        CoursePresenceId enrollmentId = new CoursePresenceId(currentUser.getId(), courseId);

        CoursePresence enrollment = coursePresenceRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição", "id", enrollmentId));

        // Validação extra (embora a chave composta já garanta)
        if (!enrollment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para cancelar esta inscrição.");
        }

        coursePresenceRepository.delete(enrollment);
    }

    /**
     * Lista os cursos em que o usuário logado está inscrito (RF-C.4.3)
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getMyEnrolledCourses(User currentUser) {
        return coursePresenceRepository.findByUserId(currentUser.getId())
                .stream()
                .map(CoursePresence::getCourse)
                .map(this::convertToResponseDTO) // Reusa o conversor do CourseService
                .collect(Collectors.toList());
    }

    /**
     * Admin: Lista os usuários inscritos em um curso específico (RF-D.5.4)
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getEnrolledUsersForCourse(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Curso", "id", courseId);
        }

        return coursePresenceRepository.findByCourseId(courseId)
                .stream()
                .map(CoursePresence::getUser)
                .map(userService::convertToResponseDTO) // Usa o conversor do UserService
                .collect(Collectors.toList());
    }


}
