package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByClienteId(Long clienteId);
}
