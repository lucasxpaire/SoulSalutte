package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
