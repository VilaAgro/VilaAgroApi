package com.vilaagro.api.repository;

import com.vilaagro.api.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para operações com a entidade Address
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
