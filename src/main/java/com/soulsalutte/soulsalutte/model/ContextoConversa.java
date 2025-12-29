package com.soulsalutte.soulsalutte.model;

import com.soulsalutte.soulsalutte.enums.EstadoConversa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ESTADO_CONVERSA")
public class ContextoConversa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONTEXTO")
    private Long id;

    @OneToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false, unique = true)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ATUAL", nullable = false)
    private EstadoConversa estadoAtual;

    @Column(name = "DATA_SELECIONADA")
    private String dataSelecionada;

    @Column(name = "HORARIO_SELECIONADO")
    private String horarioSelecionado;

    @Column(name = "SERVICO_SELECIONADO")
    private String servicoSelecionado;

    @Column(name = "ID_SESSAO_TEMPORARIA")
    private Long idSessaoTemporaria;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @Column(name = "TELEFONE_WHATSAPP")
    private String telefoneWhatsApp;
}

