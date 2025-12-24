package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByClienteId(Long clienteId);
}
