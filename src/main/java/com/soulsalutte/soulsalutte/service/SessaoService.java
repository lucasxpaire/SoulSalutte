package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.model.StatusSessao;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import com.soulsalutte.soulsalutte.repository.SessaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Transactional
    public Sessao criarSessao(Long clienteId, Sessao sessao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));
        sessao.setCliente(cliente);
        if (sessao.getStatus() == null) {
            sessao.setStatus(StatusSessao.AGENDADA);
        }

        Sessao novaSessao = sessaoRepository.save(sessao);

        // Sincroniza com Google Agenda e salva o ID do evento
        String googleEventId = googleCalendarService.adicionarEvento(novaSessao);
        if (googleEventId != null) {
            novaSessao.setGoogleCalendarEventId(googleEventId);
            sessaoRepository.save(novaSessao);
        }

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

    @Transactional
    public Sessao atualizarSessao(Long id, Sessao sessaoAtualizada) {
        Sessao sessaoExistente = sessaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada com ID: " + id));

        sessaoExistente.setNome(sessaoAtualizada.getNome());
        sessaoExistente.setDataHoraInicio(sessaoAtualizada.getDataHoraInicio());
        sessaoExistente.setDataHoraFim(sessaoAtualizada.getDataHoraFim());
        sessaoExistente.setStatus(sessaoAtualizada.getStatus());
        sessaoExistente.setNotasSessao(sessaoAtualizada.getNotasSessao());
        sessaoExistente.setNotificacao(sessaoAtualizada.isNotificacao());

        // Sincroniza a atualização com o Google Agenda
        googleCalendarService.atualizarEvento(sessaoExistente);

        return sessaoRepository.save(sessaoExistente);
    }

    @Transactional
    public void deletarSessao(Long id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada com ID: " + id));

        // Deleta o evento do Google Agenda antes de deletar a sessão
        if (sessao.getGoogleCalendarEventId() != null) {
            googleCalendarService.deletarEvento(sessao.getGoogleCalendarEventId());
        }

        sessaoRepository.deleteById(id);
    }

}