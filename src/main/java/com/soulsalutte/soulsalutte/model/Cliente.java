package com.soulsalutte.soulsalutte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLIENTE")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Long id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "TELEFONE", nullable = false)
    private String telefone;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDate dataCadastro;

    @Column(name = "SEXO")
    private String sexo;

    @Column(name = "CIDADE")
    private String cidade;

    @Column(name = "BAIRRO")
    private String bairro;

    @Column(name = "PROFISSAO")
    private String profissao;

    @Column(name = "ENDERECO_RESIDENCIAL")
    private String enderecoResidencial;

    @Column(name = "ENDERECO_COMERCIAL")
    private String enderecoComercial;

    @Column(name = "NATURALIDADE")
    private String naturalidade;

    @Column(name = "ESTADO_CIVIL")
    private String estadoCivil;
}
