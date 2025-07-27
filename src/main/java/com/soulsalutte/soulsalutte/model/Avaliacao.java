package com.soulsalutte.soulsalutte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AVALIACAO")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AVALIACAO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "DATA_AVALIACAO", nullable = false)
    private LocalDateTime dataAvaliacao;

    // Seção 1.0
    @Lob
    @Column(name = "DIAGNOSTICO_CLINICO")
    private String diagnosticoClinico;

    @Lob
    @Column(name = "DIAGNOSTICO_FISIOTERAPEUTICO")
    private String diagnosticoFisioterapeutico;

    // Seção 2.0
    @Lob
    @Column(name = "HISTORIA_CLINICA")
    private String historiaClinica;

    @Lob
    @Column(name = "QUEIXA_PRINCIPAL")
    private String queixaPrincipal;

    @Lob
    @Column(name = "HABITOS_VIDA")
    private String habitosVida;

    @Lob
    @Column(name = "HMA")
    private String hma;

    @Lob
    @Column(name = "HMP")
    private String hmp;

    @Lob
    @Column(name = "ANTECEDENTES_PESSOAIS")
    private String antecedentesPessoais;

    @Lob
    @Column(name = "ANTECEDENTES_FAMILIARES")
    private String antecedentesFamiliares;

    @Lob
    @Column(name = "TRATAMENTOS_REALIZADOS")
    private String tratamentosRealizados;

    // Seção 3.0
    // 3.1
    private boolean deambulando;
    private boolean internado;
    private boolean deambulandoComApoio;
    private boolean orientado;
    private boolean cadeiraDeRodas;

    // 3.2
    private boolean temExamesComplementares;
    @Lob
    @Column(name = "EXAMES_COMPLEMENTARES_DESCRICAO")
    private String examesComplementaresDescricao;

    // 3.3
    private boolean usaMedicamentos;
    @Lob
    @Column(name = "MEDICAMENTOS_DESCRICAO")
    private String medicamentosDescricao;

    // 3.4
    private boolean realizouCirurgia;
    @Lob
    @Column(name = "CIRURGIAS_DESCRICAO")
    private String cirurgiasDescricao;

    // 3.5
    private boolean inspecaoNormal;
    private boolean inspecaoEdema;
    private boolean inspecaoCicatrizacaoIncompleta;
    private boolean inspecaoEritemas;
    private boolean inspecaoOutros;
    @Lob
    @Column(name = "INSPECAO_DESCRICAO")
    private String inspecaoOutrosDescricao;

    // 3.6
    @Lob
    @Column(name = "SEMIOLOGIA")
    private String semiologia;

    // 3.7
    @Lob
    @Column(name = "TESTES_ESPECIFICOS")
    private String testesEspecificos;

    // 3.8
    @Column(name = "AVALIACAO_DOR_EVA")
    private int avaliacaoDor;

    // 4.0
    // 4.1
    @Lob
    @Column(name = "OBJETIVOS_TRATAMENTO", nullable = false)
    private String objetivosTratamento;

    // 4.2
    @Lob
    @Column(name = "RECURSOS_TERAPEUTICOS")
    private String recursosTerapeuticos;

    // 4.3
    @Lob
    @Column(name = "PLANO_TRATAMENTO")
    private String planoTratamento;

    // 4.4
    @Lob
    @Column(name = "EVOLUCAO")
    private String evolucao;
}
