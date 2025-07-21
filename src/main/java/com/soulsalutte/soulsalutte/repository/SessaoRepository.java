package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByClienteId(Long clienteId);
}
