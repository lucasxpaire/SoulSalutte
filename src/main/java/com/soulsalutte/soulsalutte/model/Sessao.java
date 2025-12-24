package com.soulsalutte.soulsalutte.model;

import com.soulsalutte.soulsalutte.enums.StatusSessao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SESSAO")
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SESSAO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_HORA_INICIO", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "DATA_HORA_FIM", nullable = false)
    private LocalDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private StatusSessao status;

    @Lob
    @Column(name = "NOTAS_SESSAO")
    private String notasSessao;

}
