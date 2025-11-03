package com.vilaagro.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade para feiras/eventos
 */
@Entity
@Table(name = "fairs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fair {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 10)
    private String startTime;

    @Column(nullable = false, length = 10)
    private String endTime;

    @Column(nullable = false)
    private Integer expectedMerchants = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FairStatus status = FairStatus.scheduled;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fair_attractions",
        joinColumns = @JoinColumn(name = "fair_id"),
        inverseJoinColumns = @JoinColumn(name = "attraction_id")
    )
    private List<Attraction> attractions = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
