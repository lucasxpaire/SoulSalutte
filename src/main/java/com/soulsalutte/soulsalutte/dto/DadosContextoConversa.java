package com.soulsalutte.soulsalutte.dto;

import com.soulsalutte.soulsalutte.enums.EstadoConversa;
import com.soulsalutte.soulsalutte.model.ContextoConversa;

import java.time.LocalDateTime;

public record DadosContextoConversa(
        Long id,
        Long clienteId,
        String estadoAtual,
        String dataSelecionada,
        String horarioSelecionado,
        String servicoSelecionado,
        LocalDateTime dataAtualizacao
) {
    public DadosContextoConversa(ContextoConversa contexto) {
        this(
                contexto.getId(),
                contexto.getCliente().getId(),
                contexto.getEstadoAtual().toString(),
                contexto.getDataSelecionada(),
                contexto.getHorarioSelecionado(),
                contexto.getServicoSelecionado(),
                contexto.getDataAtualizacao()
        );
    }
}

