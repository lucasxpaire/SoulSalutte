package com.soulsalutte.soulsalutte.controller;

import com.soulsalutte.soulsalutte.model.Avaliacao;
import com.soulsalutte.soulsalutte.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Avaliacao> criarAvaliacao(@PathVariable Long clienteId, @RequestBody Avaliacao avaliacao) {
        Avaliacao novaAvaliacao = avaliacaoService.criarAvaliacao(clienteId, avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesDoCliente(@PathVariable Long clienteId) {
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorCliente(clienteId);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscarAvaliacaoPorId(@PathVariable Long id) {
        Avaliacao avaliacao = avaliacaoService.buscarPorId(id);
        return ResponseEntity.ok(avaliacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizarAvaliacao(@PathVariable Long id, @RequestBody Avaliacao avaliacao) {
        Avaliacao avaliacaoAtualizada = avaliacaoService.atualizarAvaliacao(id, avaliacao);
        return ResponseEntity.ok(avaliacaoAtualizada);
    }

    @PostMapping("/{id}/evolucoes")
    public ResponseEntity<Avaliacao> adicionarEvolucao(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String textoEvolucao = payload.get("evolucao");
        if (textoEvolucao == null || textoEvolucao.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Avaliacao avaliacaoAtualizada = avaliacaoService.adicionarEvolucao(id, textoEvolucao);
        return ResponseEntity.ok(avaliacaoAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long id) {
        avaliacaoService.deletarAvaliacao(id);
        return ResponseEntity.noContent().build();
    }
}
