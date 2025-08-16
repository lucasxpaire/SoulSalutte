package com.soulsalutte.soulsalutte.controller;

import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.service.SessaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessoes")
public class SessaoController {

    @Autowired
    private SessaoService sessaoService;

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Sessao> criarSessao(@PathVariable Long clienteId, @RequestBody Sessao sessao) {
        Sessao novaSessao = sessaoService.criarSessao(clienteId, sessao);
        return ResponseEntity.ok(novaSessao);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Sessao>> listarSessoesPorCliente(@PathVariable Long clienteId) {
        List<Sessao> sessoes = sessaoService.listarSessaoPorCliente(clienteId);
        return ResponseEntity.ok(sessoes);
    }

    @GetMapping
    public ResponseEntity<List<Sessao>> listarTodasAsSessoes() {
        List<Sessao> sessoes = sessaoService.listarTodasAsSessoes();
        return ResponseEntity.ok(sessoes);
    }
}
