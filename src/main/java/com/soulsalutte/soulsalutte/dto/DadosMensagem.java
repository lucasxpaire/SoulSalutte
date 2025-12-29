package com.soulsalutte.soulsalutte.dto;

import com.soulsalutte.soulsalutte.enums.StatusMensagem;
import com.soulsalutte.soulsalutte.model.Mensagem;

import java.time.LocalDateTime;

public record DadosMensagem(
        Long id,
        Long clienteId,
        String conteudo,
        LocalDateTime dataHora,
        String tipoOrigem,
        String status,
        String idMensagemWhatsApp
) {
    public DadosMensagem(Mensagem mensagem) {
        this(
                mensagem.getId(),
                mensagem.getCliente().getId(),
                mensagem.getConteudo(),
                mensagem.getDataHora(),
                mensagem.getTipoOrigem().toString(),
                mensagem.getStatus().toString(),
                mensagem.getIdMensagemWhatsApp()
        );
    }
}


