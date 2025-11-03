package com.vilaagro.api.repository;

import com.vilaagro.api.model.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {

    /**
     * Lista todos os comunicados, dos mais novos para os mais antigos
     * (RF-C.1.3 - Comerciantes precisam ver os últimos comunicados)
     */
    List<Statement> findAllByOrderByCreatedAtDesc();
}