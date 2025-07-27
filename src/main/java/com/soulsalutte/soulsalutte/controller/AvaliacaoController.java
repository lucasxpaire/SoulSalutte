package com.soulsalutte.soulsalutte.controller;

import com.soulsalutte.soulsalutte.model.Avaliacao;
import com.soulsalutte.soulsalutte.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
