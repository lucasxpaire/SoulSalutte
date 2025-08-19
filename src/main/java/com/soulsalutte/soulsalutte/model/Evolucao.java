package com.soulsalutte.soulsalutte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evolucao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVOLUCAO")
    private Long id;

    @Lob
    @Column(name = "EVOLUCAO")
    private String evolucao;

    @CreationTimestamp
    @Column(name = "DATA_EVOLUCAO")
    private LocalDateTime dataEvolucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVALIACAO_ID", nullable = false)
    @JsonIgnore
    private Avaliacao avaliacao;
}
