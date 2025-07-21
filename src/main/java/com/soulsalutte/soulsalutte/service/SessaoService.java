package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import com.soulsalutte.soulsalutte.repository.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessaoService {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private ClienteRepository clienteService;

    public Sessao criarSessao(Long clienteId, Sessao sessao) {
        Cliente cliente = clienteService.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));
        sessao.setCliente(cliente);
        return sessaoRepository.save(sessao);
    }

    public List<Sessao> listarSessaoPorCliente(Long clienteId) {
        return sessaoRepository.findByClienteId(clienteId);
    }

}
