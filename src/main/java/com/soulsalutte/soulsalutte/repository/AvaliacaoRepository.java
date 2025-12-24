package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByClienteId(Long clienteId);
}
