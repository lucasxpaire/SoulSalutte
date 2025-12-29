package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByClienteIdOrderByDataHoraDesc(Long clienteId);

    List<Mensagem> findByClienteIdAndIdMensagemWhatsAppIsNotNull(Long clienteId);
}


