package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.enums.StatusSessao;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import com.soulsalutte.soulsalutte.repository.SessaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessaoService {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Sessao criarSessao(Long clienteId, Sessao sessao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));
        sessao.setCliente(cliente);
        if (sessao.getStatus() == null) {
            sessao.setStatus(StatusSessao.AGENDADA);
        }

        return sessaoRepository.save(sessao);
    }

    public List<Sessao> listarSessaoPorCliente(Long clienteId) {
        return sessaoRepository.findByClienteId(clienteId);
    }

    public List<Sessao> listarTodasAsSessoes() {
        return sessaoRepository.findAll();
    }

    @Transactional
    public Sessao atualizarSessao(Long id, Sessao sessaoAtualizada) {
        Sessao sessaoExistente = sessaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada com ID: " + id));

        sessaoExistente.setNome(sessaoAtualizada.getNome());
        sessaoExistente.setDataHoraInicio(sessaoAtualizada.getDataHoraInicio());
        sessaoExistente.setDataHoraFim(sessaoAtualizada.getDataHoraFim());
        sessaoExistente.setStatus(sessaoAtualizada.getStatus());
        sessaoExistente.setNotasSessao(sessaoAtualizada.getNotasSessao());

        return sessaoRepository.save(sessaoExistente);
    }

    @Transactional
    public void deletarSessao(Long id) {
        sessaoRepository.deleteById(id);
    }

}