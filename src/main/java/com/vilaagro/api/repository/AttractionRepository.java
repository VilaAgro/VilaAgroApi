package com.vilaagro.api.repository;

import com.vilaagro.api.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, UUID> {
    List<Attraction> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
}
