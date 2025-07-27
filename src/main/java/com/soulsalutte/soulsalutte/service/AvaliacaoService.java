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

    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada com ID: " + id));
    }

    public Avaliacao atualizarAvaliacao(Long id, Avaliacao avaliacaoAtualizada) {
        Avaliacao avaliacaoExistente = buscarPorId(id);

        // Seção 1.0 e 2.0
        avaliacaoExistente.setDataAvaliacao(avaliacaoAtualizada.getDataAvaliacao());
        avaliacaoExistente.setDiagnosticoClinico(avaliacaoAtualizada.getDiagnosticoClinico());
        avaliacaoExistente.setDiagnosticoFisioterapeutico(avaliacaoAtualizada.getDiagnosticoFisioterapeutico());
        avaliacaoExistente.setHistoriaClinica(avaliacaoAtualizada.getHistoriaClinica());
        avaliacaoExistente.setQueixaPrincipal(avaliacaoAtualizada.getQueixaPrincipal());
        avaliacaoExistente.setHabitosVida(avaliacaoAtualizada.getHabitosVida());
        avaliacaoExistente.setHma(avaliacaoAtualizada.getHma());
        avaliacaoExistente.setHmp(avaliacaoAtualizada.getHmp());
        avaliacaoExistente.setAntecedentesPessoais(avaliacaoAtualizada.getAntecedentesPessoais());
        avaliacaoExistente.setAntecedentesFamiliares(avaliacaoAtualizada.getAntecedentesFamiliares());
        avaliacaoExistente.setTratamentosRealizados(avaliacaoAtualizada.getTratamentosRealizados());

        // Seção 3.0
        avaliacaoExistente.setDeambulando(avaliacaoAtualizada.isDeambulando());
        avaliacaoExistente.setDeambulandoComApoio(avaliacaoAtualizada.isDeambulandoComApoio());
        avaliacaoExistente.setCadeiraDeRodas(avaliacaoAtualizada.isCadeiraDeRodas());
        avaliacaoExistente.setInternado(avaliacaoAtualizada.isInternado());
        avaliacaoExistente.setOrientado(avaliacaoAtualizada.isOrientado());
        avaliacaoExistente.setTemExamesComplementares(avaliacaoAtualizada.isTemExamesComplementares());
        avaliacaoExistente.setExamesComplementaresDescricao(avaliacaoAtualizada.getExamesComplementaresDescricao());
        avaliacaoExistente.setUsaMedicamentos(avaliacaoAtualizada.isUsaMedicamentos());
        avaliacaoExistente.setMedicamentosDescricao(avaliacaoAtualizada.getMedicamentosDescricao());
        avaliacaoExistente.setRealizouCirurgia(avaliacaoAtualizada.isRealizouCirurgia());
        avaliacaoExistente.setCirurgiasDescricao(avaliacaoAtualizada.getCirurgiasDescricao());

        // Seção 3.5
        avaliacaoExistente.setInspecaoNormal(avaliacaoAtualizada.isInspecaoNormal());
        avaliacaoExistente.setInspecaoEdema(avaliacaoAtualizada.isInspecaoEdema());
        avaliacaoExistente.setInspecaoCicatrizacaoIncompleta(avaliacaoAtualizada.isInspecaoCicatrizacaoIncompleta());
        avaliacaoExistente.setInspecaoEritemas(avaliacaoAtualizada.isInspecaoEritemas());
        avaliacaoExistente.setInspecaoOutros(avaliacaoAtualizada.isInspecaoOutros());
        avaliacaoExistente.setInspecaoOutrosDescricao(avaliacaoAtualizada.getInspecaoOutrosDescricao());

        // Seção 3.6, 3.7, 3.8
        avaliacaoExistente.setSemiologia(avaliacaoAtualizada.getSemiologia());
        avaliacaoExistente.setTestesEspecificos(avaliacaoAtualizada.getTestesEspecificos());
        avaliacaoExistente.setAvaliacaoDor(avaliacaoAtualizada.getAvaliacaoDor());

        // Seção 4.0
        avaliacaoExistente.setObjetivosTratamento(avaliacaoAtualizada.getObjetivosTratamento());
        avaliacaoExistente.setRecursosTerapeuticos(avaliacaoAtualizada.getRecursosTerapeuticos());
        avaliacaoExistente.setPlanoTratamento(avaliacaoAtualizada.getPlanoTratamento());
        avaliacaoExistente.setEvolucao(avaliacaoAtualizada.getEvolucao());

        return avaliacaoRepository.save(avaliacaoExistente);
    }
}
