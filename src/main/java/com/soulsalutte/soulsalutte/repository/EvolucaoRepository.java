package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Evolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvolucaoRepository extends JpaRepository<Evolucao, Long> {
}
