package com.soulsalutte.soulsalutte.repository;

import com.soulsalutte.soulsalutte.model.ContextoConversa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContextoConversaRepository extends JpaRepository<ContextoConversa, Long> {
    Optional<ContextoConversa> findByClienteId(Long clienteId);

    Optional<ContextoConversa> findByTelefoneWhatsApp(String telefoneWhatsApp);
}

