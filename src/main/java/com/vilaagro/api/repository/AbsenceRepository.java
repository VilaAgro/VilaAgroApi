package com.vilaagro.api.repository;

import com.vilaagro.api.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, UUID> {
    List<Absence> findByUserIdOrderByDateDesc(UUID userId);
    Optional<Absence> findByUserIdAndDate(UUID userId, LocalDate date);
}