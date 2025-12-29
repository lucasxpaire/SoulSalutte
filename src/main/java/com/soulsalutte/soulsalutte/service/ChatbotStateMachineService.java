package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.config.WhatsAppConfig;
import com.soulsalutte.soulsalutte.enums.EstadoConversa;
import com.soulsalutte.soulsalutte.enums.StatusMensagem;
import com.soulsalutte.soulsalutte.enums.StatusSessao;
import com.soulsalutte.soulsalutte.model.Cliente;
import com.soulsalutte.soulsalutte.model.ContextoConversa;
import com.soulsalutte.soulsalutte.model.Mensagem;
import com.soulsalutte.soulsalutte.model.Sessao;
import com.soulsalutte.soulsalutte.repository.ClienteRepository;
import com.soulsalutte.soulsalutte.repository.ContextoConversaRepository;
import com.soulsalutte.soulsalutte.repository.MensagemRepository;
import com.soulsalutte.soulsalutte.repository.SessaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatbotStateMachineService {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotStateMachineService.class);

    private final ContextoConversaRepository contextoConversaRepository;
    private final MensagemRepository mensagemRepository;
    private final ClienteRepository clienteRepository;
    private final SessaoRepository sessaoRepository;
    private final SessaoService sessaoService;

    public ChatbotStateMachineService(
            ContextoConversaRepository contextoConversaRepository,
            MensagemRepository mensagemRepository,
            ClienteRepository clienteRepository,
            SessaoRepository sessaoRepository,
            SessaoService sessaoService
    ) {
        this.contextoConversaRepository = contextoConversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.clienteRepository = clienteRepository;
        this.sessaoRepository = sessaoRepository;
        this.sessaoService = sessaoService;
    }

    /**
     * Processa uma mensagem recebida do WhatsApp e retorna a resposta do sistema
     */
    public String processarMensagemWhatsApp(
            String telefoneWhatsApp,
            String nomeCliente,
            String conteudoMensagem,
            String idMensagemWhatsApp
    ) {
        try {
            logger.info("Processando mensagem de {}: {}", telefoneWhatsApp, conteudoMensagem);

            // Buscar ou criar cliente
            Cliente cliente = buscarOuCriarCliente(telefoneWhatsApp, nomeCliente);

            // Salvar mensagem recebida
            salvarMensagem(cliente, conteudoMensagem, Mensagem.TipoOrigem.CLIENTE, StatusMensagem.ENTREGUE, idMensagemWhatsApp);

            // Obter ou criar contexto da conversa
            ContextoConversa contexto = obterOuCriarContextoConversa(cliente, telefoneWhatsApp);

            // Processar pela máquina de estados
            String respostaBot = processarEstado(contexto, conteudoMensagem, cliente);

            // Salvar resposta do sistema
            salvarMensagem(cliente, respostaBot, Mensagem.TipoOrigem.SISTEMA, StatusMensagem.ENVIADA, null);

            return respostaBot;
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do WhatsApp", e);
            return "Desculpe, ocorreu um erro. Tente novamente mais tarde.";
        }
    }

    /**
     * Processa o estado atual da conversa e retorna a resposta apropriada
     */
    private String processarEstado(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        EstadoConversa estadoAtual = contexto.getEstadoAtual();
        String mensagemLower = mensagem.toLowerCase().trim();

        return switch (estadoAtual) {
            case LIVRE -> processarEstadoLivre(contexto, mensagemLower, cliente);
            case AGUARDANDO_NOME -> processarEstadoAguardandoNome(contexto, mensagem, cliente);
            case AGUARDANDO_EMAIL -> processarEstadoAguardandoEmail(contexto, mensagem, cliente);
            case AGUARDANDO_DATA_NASCIMENTO -> processarEstadoAguardandoDataNascimento(contexto, mensagem, cliente);
            case AGUARDANDO_TELEFONE -> processarEstadoAguardandoTelefone(contexto, mensagem, cliente);
            case ESCOLHENDO_SERVICO -> processarEstadoEscolhendoServico(contexto, mensagemLower, cliente);
            case ESCOLHENDO_DATA -> processarEstadoEscolhendoData(contexto, mensagemLower, cliente);
            case ESCOLHENDO_HORARIO -> processarEstadoEscolhendoHorario(contexto, mensagemLower, cliente);
            case CONFIRMANDO_AGENDAMENTO -> processarEstadoConfirmandoAgendamento(contexto, mensagemLower, cliente);
            case AGENDAMENTO_CONCLUIDO -> "Seu agendamento foi confirmado! 🎉";
            case CANCELANDO_AGENDAMENTO -> processarEstadoCancelandoAgendamento(contexto, mensagemLower, cliente);
            case ALTERANDO_AGENDAMENTO -> processarEstadoAlterandoAgendamento(contexto, mensagemLower, cliente);
        };
    }

    private String processarEstadoLivre(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        // Detectar intenção pela mensagem
        if (contemPalavrasChave(mensagem, WhatsAppConfig.PALAVRAS_CHAVE_AGENDAR)) {
            contexto.setEstadoAtual(EstadoConversa.ESCOLHENDO_DATA);
            contextoConversaRepository.save(contexto);
            return obterListaDiasDisponiveis();
        } else if (contemPalavrasChave(mensagem, WhatsAppConfig.PALAVRAS_CHAVE_CANCELAR)) {
            contexto.setEstadoAtual(EstadoConversa.CANCELANDO_AGENDAMENTO);
            contextoConversaRepository.save(contexto);
            return obterListaSessoesFuturas(cliente);
        } else if (contemPalavrasChave(mensagem, WhatsAppConfig.PALAVRAS_CHAVE_ALTERAR)) {
            contexto.setEstadoAtual(EstadoConversa.ALTERANDO_AGENDAMENTO);
            contextoConversaRepository.save(contexto);
            return obterListaSessoesFuturas(cliente);
        } else if (contemPalavrasChave(mensagem, WhatsAppConfig.PALAVRAS_CHAVE_SAUDACAO)) {
            return "Olá! 👋 Como posso ajudá-lo? Você pode:\n" +
                    "1️⃣ Agendar uma consulta\n" +
                    "2️⃣ Cancelar agendamento\n" +
                    "3️⃣ Alterar horário\n" +
                    "Qual você prefere?";
        }

        return "Desculpe, não entendi. Pode tentar novamente? 😊";
    }

    private String processarEstadoAguardandoNome(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        cliente.setNome(mensagem);
        clienteRepository.save(cliente);
        contexto.setEstadoAtual(EstadoConversa.AGUARDANDO_EMAIL);
        contextoConversaRepository.save(contexto);
        return "Obrigado! Qual é seu email?";
    }

    private String processarEstadoAguardandoEmail(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        if (!isValidEmail(mensagem)) {
            return "Email inválido. Por favor, digite um email válido.";
        }

        cliente.setEmail(mensagem);
        clienteRepository.save(cliente);
        contexto.setEstadoAtual(EstadoConversa.LIVRE);
        contextoConversaRepository.save(contexto);
        return "Perfeito! 😊 Seu cadastro foi atualizado. Em que posso ajudá-lo?";
    }

    private String processarEstadoAguardandoDataNascimento(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        // Implementar validação de data
        contexto.setEstadoAtual(EstadoConversa.LIVRE);
        contextoConversaRepository.save(contexto);
        return "Data registrada! 📅 Em que mais posso ajudá-lo?";
    }

    private String processarEstadoAguardandoTelefone(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        cliente.setTelefone(mensagem);
        clienteRepository.save(cliente);
        contexto.setEstadoAtual(EstadoConversa.LIVRE);
        contextoConversaRepository.save(contexto);
        return "Telefone atualizado! ☎️ Em que posso ajudá-lo?";
    }

    private String processarEstadoEscolhendoServico(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        contexto.setServicoSelecionado(mensagem);
        contexto.setEstadoAtual(EstadoConversa.ESCOLHENDO_DATA);
        contextoConversaRepository.save(contexto);
        return obterListaDiasDisponiveis();
    }

    private String processarEstadoEscolhendoData(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        contexto.setDataSelecionada(mensagem);
        contexto.setEstadoAtual(EstadoConversa.ESCOLHENDO_HORARIO);
        contextoConversaRepository.save(contexto);
        return obterListaHorariosDisponiveis(mensagem);
    }

    private String processarEstadoEscolhendoHorario(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        contexto.setHorarioSelecionado(mensagem);
        contexto.setEstadoAtual(EstadoConversa.CONFIRMANDO_AGENDAMENTO);
        contextoConversaRepository.save(contexto);

        return String.format(
                "Resumo do seu agendamento:\n" +
                        "📅 Data: %s\n" +
                        "⏰ Horário: %s\n" +
                        "Confirma? Digite SIM ou NÃO.",
                contexto.getDataSelecionada(),
                contexto.getHorarioSelecionado()
        );
    }

    private String processarEstadoConfirmandoAgendamento(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        if (mensagem.equals("sim")) {
            // Criar a sessão
            criarSessaoDoContexto(contexto, cliente);
            contexto.setEstadoAtual(EstadoConversa.AGENDAMENTO_CONCLUIDO);
            contextoConversaRepository.save(contexto);
            return "Seu agendamento foi solicitado com sucesso! ✅\n" +
                    "Você receberá uma confirmação em breve.";
        } else if (mensagem.equals("não") || mensagem.equals("nao")) {
            contexto.setEstadoAtual(EstadoConversa.LIVRE);
            contextoConversaRepository.save(contexto);
            return "Entendido. Em que mais posso ajudá-lo?";
        } else {
            return "Por favor, digite SIM ou NÃO para confirmar.";
        }
    }

    private String processarEstadoCancelandoAgendamento(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        // Implementar cancelamento
        contexto.setEstadoAtual(EstadoConversa.LIVRE);
        contextoConversaRepository.save(contexto);
        return "Agendamento cancelado com sucesso. 📋";
    }

    private String processarEstadoAlterandoAgendamento(
            ContextoConversa contexto,
            String mensagem,
            Cliente cliente
    ) {
        // Implementar alteração
        contexto.setEstadoAtual(EstadoConversa.ESCOLHENDO_DATA);
        contextoConversaRepository.save(contexto);
        return "Qual é a nova data que você prefere?";
    }

    // ============= Métodos auxiliares =============

    private Cliente buscarOuCriarCliente(String telefoneWhatsApp, String nomeCliente) {
        return clienteRepository.findByTelefone(telefoneWhatsApp)
                .orElseGet(() -> {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setTelefone(telefoneWhatsApp);
                    novoCliente.setNome(nomeCliente != null ? nomeCliente : "Usuário WhatsApp");
                    novoCliente.setEmail("whatsapp_" + telefoneWhatsApp + "@temp.local");
                    return clienteRepository.save(novoCliente);
                });
    }

    private ContextoConversa obterOuCriarContextoConversa(Cliente cliente, String telefoneWhatsApp) {
        return contextoConversaRepository.findByClienteId(cliente.getId())
                .orElseGet(() -> {
                    ContextoConversa novoContexto = new ContextoConversa();
                    novoContexto.setCliente(cliente);
                    novoContexto.setTelefoneWhatsApp(telefoneWhatsApp);
                    novoContexto.setEstadoAtual(EstadoConversa.LIVRE);
                    return contextoConversaRepository.save(novoContexto);
                });
    }

    private void salvarMensagem(
            Cliente cliente,
            String conteudo,
            Mensagem.TipoOrigem tipoOrigem,
            StatusMensagem status,
            String idMensagemWhatsApp
    ) {
        Mensagem mensagem = new Mensagem();
        mensagem.setCliente(cliente);
        mensagem.setConteudo(conteudo);
        mensagem.setTipoOrigem(tipoOrigem);
        mensagem.setStatus(status);
        mensagem.setIdMensagemWhatsApp(idMensagemWhatsApp);
        mensagemRepository.save(mensagem);
    }

    private boolean contemPalavrasChave(String mensagem, String[] palavrasChave) {
        for (String palavra : palavrasChave) {
            if (mensagem.contains(palavra.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String obterListaDiasDisponiveis() {
        return "Qual dia você prefere?\n" +
                "1️⃣ Segunda\n" +
                "2️⃣ Terça\n" +
                "3️⃣ Quarta\n" +
                "4️⃣ Quinta\n" +
                "5️⃣ Sexta";
    }

    private String obterListaHorariosDisponiveis(String data) {
        return "Qual horário você prefere?\n" +
                "1️⃣ 08:00\n" +
                "2️⃣ 09:00\n" +
                "3️⃣ 10:00\n" +
                "4️⃣ 14:00\n" +
                "5️⃣ 15:00";
    }

    private String obterListaSessoesFuturas(Cliente cliente) {
        List<Sessao> sessoes = sessaoRepository.findByClienteIdAndStatusNot(
                cliente.getId(),
                StatusSessao.CANCELADA
        );

        if (sessoes.isEmpty()) {
            return "Você não possui agendamentos no momento.";
        }

        StringBuilder resposta = new StringBuilder("Suas consultas agendadas:\n");
        for (int i = 0; i < sessoes.size(); i++) {
            Sessao sessao = sessoes.get(i);
            resposta.append(String.format(
                    "%d️⃣ %s - %s\n",
                    i + 1,
                    sessao.getDataHoraInicio(),
                    sessao.getNome()
            ));
        }
        resposta.append("Digite o número para selecionar.");
        return resposta.toString();
    }

    private void criarSessaoDoContexto(ContextoConversa contexto, Cliente cliente) {
        Sessao sessao = new Sessao();
        sessao.setCliente(cliente);
        sessao.setNome("Agendamento via WhatsApp");
        sessao.setStatus(StatusSessao.SOLICITADA);
        // Parsear data e hora do contexto
        // Por agora, deixar como exemplo
        sessao.setDataHoraInicio(LocalDateTime.now().plusDays(1));
        sessao.setDataHoraFim(LocalDateTime.now().plusDays(1).plusHours(1));
        sessaoRepository.save(sessao);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}

