package com.soulsalutte.soulsalutte.controller;

import com.soulsalutte.soulsalutte.dto.DadosWebhookWhatsApp;
import com.soulsalutte.soulsalutte.service.ChatbotStateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WhatsAppWebhookController {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final ChatbotStateMachineService chatbotService;

    public WhatsAppWebhookController(ChatbotStateMachineService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * Endpoint para verificação do webhook pela Meta
     * GET /api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=...&hub.verify_token=...
     */
    @GetMapping("/whatsapp")
    public ResponseEntity<?> verificarWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String verifyToken
    ) {
        logger.info("Recebido pedido de verificação de webhook");

        // Validar token (ajuste conforme sua configuração)
        String tokenConfigurando = "seuTokenAqui"; // Idealmente, vir de @Value
        if (!tokenConfigurando.equals(verifyToken)) {
            logger.warn("Token de verificação inválido");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("subscribe".equals(mode)) {
            logger.info("Webhook verificado com sucesso");
            return ResponseEntity.ok(challenge);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Endpoint para receber mensagens do WhatsApp
     * POST /api/webhook/whatsapp
     */
    @PostMapping("/whatsapp")
    public ResponseEntity<?> receberMensagem(@RequestBody DadosWebhookWhatsApp payload) {
        logger.info("Recebida notificação do WhatsApp");

        try {
            if (payload == null || payload.entry() == null) {
                logger.warn("Payload vazio ou inválido");
                return ResponseEntity.ok().build();
            }

            // Processar cada entry no payload
            for (DadosWebhookWhatsApp.Entry entry : payload.entry()) {
                if (entry.changes() == null) {
                    continue;
                }

                for (DadosWebhookWhatsApp.Change change : entry.changes()) {
                    processarChange(change);
                }
            }

            // Sempre retornar 200 OK para a Meta
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Erro ao processar webhook do WhatsApp", e);
            // Retornar 200 OK mesmo em caso de erro (Meta não tenta reenviar)
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Processa uma mudança (change) do webhook
     */
    private void processarChange(DadosWebhookWhatsApp.Change change) {
        if (change.value() == null) {
            return;
        }

        DadosWebhookWhatsApp.Value value = change.value();

        // Processar apenas mensagens de entrada
        if (value.messages() != null) {
            processarMensagens(value.messages(), value.contacts());
        }
    }

    /**
     * Processa as mensagens recebidas
     */
    private void processarMensagens(
            DadosWebhookWhatsApp.Message[] messages,
            DadosWebhookWhatsApp.Contact[] contacts
    ) {
        if (messages == null || messages.length == 0) {
            return;
        }

        for (DadosWebhookWhatsApp.Message message : messages) {
            processarMensagemIndividual(message, contacts);
        }
    }

    /**
     * Processa uma mensagem individual
     */
    private void processarMensagemIndividual(
            DadosWebhookWhatsApp.Message message,
            DadosWebhookWhatsApp.Contact[] contacts
    ) {
        try {
            if (message.text() == null || message.text().body() == null) {
                logger.warn("Mensagem de texto vazia");
                return;
            }

            String telefoneOriginario = message.from();
            String conteudoMensagem = message.text().body();
            String idMensagemWhatsApp = message.id();

            // Obter nome do contato se disponível
            String nomeContato = extrairNomeContato(contacts, telefoneOriginario);

            logger.info("Processando mensagem de {}: {}", telefoneOriginario, conteudoMensagem);

            // Processar via chatbot
            String respostaBotS = chatbotService.processarMensagemWhatsApp(
                    telefoneOriginario,
                    nomeContato,
                    conteudoMensagem,
                    idMensagemWhatsApp
            );

            logger.info("Resposta do bot: {}", respostaBotS);

            // TODO: Implementar envio da resposta de volta via WhatsApp API
            // enviarMensagemWhatsApp(telefoneOriginario, respostaBotS);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem individual", e);
        }
    }

    /**
     * Extrai o nome do contato a partir da lista de contatos
     */
    private String extrairNomeContato(
            DadosWebhookWhatsApp.Contact[] contacts,
            String telefoneOriginario
    ) {
        if (contacts == null || contacts.length == 0) {
            return null;
        }

        for (DadosWebhookWhatsApp.Contact contact : contacts) {
            if (telefoneOriginario.equals(contact.wa_id()) && contact.profile() != null) {
                return contact.profile().name();
            }
        }

        return null;
    }

    /**
     * Método auxiliar para enviar mensagem via WhatsApp API
     * TODO: Implementar integração com WhatsApp Business API
     */
    private void enviarMensagemWhatsApp(String telefone, String mensagem) {
        // Implementar chamada à WhatsApp Business API
        logger.info("TODO: Enviar mensagem para {} com conteúdo: {}", telefone, mensagem);
    }

    /**
     * Endpoint para health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "WhatsApp Webhook");
        return ResponseEntity.ok(response);
    }
}

