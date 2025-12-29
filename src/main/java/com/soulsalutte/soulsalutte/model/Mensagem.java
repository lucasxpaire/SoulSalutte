package com.soulsalutte.soulsalutte.model;

import com.soulsalutte.soulsalutte.enums.StatusMensagem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MENSAGEM")
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MENSAGEM")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "CONTEUDO", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @CreationTimestamp
    @Column(name = "DATA_HORA", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ORIGEM", nullable = false)
    private TipoOrigem tipoOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private StatusMensagem status;

    @Column(name = "ID_MENSAGEM_WHATSAPP")
    private String idMensagemWhatsApp;

    public enum TipoOrigem {
        CLIENTE,
        SISTEMA
    }
}

