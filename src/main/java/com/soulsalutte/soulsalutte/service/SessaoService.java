package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.model.StatusSessao;
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
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    public Sessao criarSessao(Long clienteId, Sessao sessao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));
        sessao.setCliente(cliente);
        if (sessao.getStatus() == null) {
            sessao.setStatus(StatusSessao.AGENDADA);
        }
        Sessao novaSessao = sessaoRepository.save(sessao);

        if (novaSessao.isNotificacao()) {
            emailService.enviarNotificacaoAgendamento(novaSessao);
        }
        return novaSessao;
    }

    public List<Sessao> listarSessaoPorCliente(Long clienteId) {
        return sessaoRepository.findByClienteId(clienteId);
    }

    public List<Sessao> listarTodasAsSessoes() {
        return sessaoRepository.findAll();
    }
}
