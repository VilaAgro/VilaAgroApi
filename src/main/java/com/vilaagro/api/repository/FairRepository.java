package com.vilaagro.api.repository;

import com.vilaagro.api.model.Fair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FairRepository extends JpaRepository<Fair, UUID> {
    Optional<Fair> findFirstByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
    
    @Query("SELECT f FROM Fair f WHERE MONTH(f.date) = :month AND YEAR(f.date) = :year ORDER BY f.date ASC")
    List<Fair> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
