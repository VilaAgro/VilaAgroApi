package com.vilaagro.api.repository;

import com.vilaagro.api.model.SalePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalePointRepository extends JpaRepository<SalePoint, UUID> {
}