package com.vilaagro.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um Ponto de Venda (Spot)
 * Referência: usecases.txt (RF-D.4)
 */
@Entity
@Table(name = "sale_point")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // O 'admin_id' no db.sql parece ser quem criou,
    // mas não é estritamente necessário para o CRUD básico de alocação.
    // Vamos focar no addressId e no name.

    @Column(nullable = false)
    private String name;

    @Column(name = "address_id")
    private UUID addressId; // Referencia a entidade Address

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "sale_point_id", insertable = false, updatable = false)
    private User allocatedUser; // Usuário atualmente alocado neste ponto

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}