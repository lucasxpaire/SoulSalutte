package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.enums.StatusSessao;
import com.soulsalutte.soulsalutte.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByClienteId(Long clienteId);

    List<Sessao> findByClienteIdAndStatusNot(Long clienteId, StatusSessao status);

    List<Sessao> findByClienteIdAndStatus(Long clienteId, StatusSessao status);
}
