package com.vilaagro.api.repository;

import com.vilaagro.api.model.User;
import com.vilaagro.api.model.UserType;
import com.vilaagro.api.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações com a entidade User
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário pelo email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email especificado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por tipo
     */
    List<User> findByType(UserType type);

    /**
     * Busca usuários por ponto de venda
     */
    List<User> findBySalePointId(UUID salePointId);

    /**
     * Busca usuários por status dos documentos (PENDENTE, APROVADO, etc.)
     */
    List<User> findByDocumentsStatus(AccountStatus documentsStatus);

    long countByDocumentsStatus(AccountStatus documentsStatus);
}
