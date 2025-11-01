package com.vilaagro.api.repository;

import com.vilaagro.api.model.JustificationForAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JustificationRepository extends JpaRepository<JustificationForAbsence, UUID> {
    boolean existsByAbsenceId(UUID absenceId);

    // Busca justificativas pendentes de análise (RF-D.1.5)
    @Query("SELECT j FROM JustificationForAbsence j WHERE j.isApproved IS NULL OR j.isApproved = false")
    List<JustificationForAbsence> findPendingJustifications();
}