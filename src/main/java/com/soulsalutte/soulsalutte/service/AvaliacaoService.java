package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Avaliacao;
import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.repository.AvaliacaoRepository;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Avaliacao criarAvaliacao(Long clienteId, Avaliacao avaliacao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));
        avaliacao.setCliente(cliente);
        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarAvaliacoesPorCliente(Long clienteId) {
        return avaliacaoRepository.findByClienteId(clienteId);
    }
}
