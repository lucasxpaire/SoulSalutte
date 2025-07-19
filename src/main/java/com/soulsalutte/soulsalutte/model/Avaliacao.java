package com.soulsalutte.soulsalutte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AVALIACAO")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AVALIACAO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "DATA_AVALIACAO", nullable = false)
    private LocalDateTime dataAvaliacao;

    @Lob
    @Column(name = "QUEIXA_PRINCIPAL", nullable = false)
    private String queixaPrincipal;

    @Lob
    @Column(name = "DIAGNOSTICO_FISIOTERAPEUTICO", nullable = false)
    private String diagnosticoFisioterapeutico;

    @Lob
    @Column(name = "OBJETIVOS_TRATAMENTO", nullable = false)
    private String objetivosTratamento;
}
