package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente criarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> buscarClientes(String nome) {
        if (nome != null && !nome.isEmpty()) {
            return clienteRepository.findByNomeContainingIgnoreCase(nome);
        }
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
    }

    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setSexo(clienteAtualizado.getSexo());
        clienteExistente.setCidade(clienteAtualizado.getCidade());
        clienteExistente.setBairro(clienteAtualizado.getBairro());
        clienteExistente.setProfissao(clienteAtualizado.getProfissao());
        clienteExistente.setEnderecoResidencial(clienteAtualizado.getEnderecoResidencial());
        clienteExistente.setEnderecoComercial(clienteAtualizado.getEnderecoComercial());
        clienteExistente.setNaturalidade(clienteAtualizado.getNaturalidade());
        clienteExistente.setEstadoCivil(clienteAtualizado.getEstadoCivil());

        return clienteRepository.save(clienteExistente);
    }

    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }
        try {
            clienteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Não é possível excluir o cliente pois ele possui avaliações ou sessões associadas.");
        }
    }
}
