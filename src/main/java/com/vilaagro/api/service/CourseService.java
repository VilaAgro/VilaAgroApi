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
}
