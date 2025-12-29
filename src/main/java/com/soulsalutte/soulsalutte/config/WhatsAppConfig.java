package com.soulsalutte.soulsalutte.config;

public class WhatsAppConfig {
    public static final String WEBHOOK_TOKEN = "${whatsapp.webhook.token}";
    public static final String WEBHOOK_PATH = "/api/webhook/whatsapp";
    public static final String WEBHOOK_VERIFY_PATH = "/api/webhook/whatsapp/verify";

    // Palavras-chave para detecção de intenção
    public static final String[] PALAVRAS_CHAVE_AGENDAR = {
            "marcar", "agendar", "consulta", "sessão", "agendamento",
            "quero agendar", "preciso marcar", "gostaria de agendar"
    };

    public static final String[] PALAVRAS_CHAVE_CANCELAR = {
            "cancelar", "desmarcar", "cancelamento", "não posso ir",
            "não vou poder", "quero cancelar"
    };

    public static final String[] PALAVRAS_CHAVE_ALTERAR = {
            "mudar", "remarcar", "trocar", "alterar", "outra hora",
            "outro horário", "outro dia"
    };

    public static final String[] PALAVRAS_CHAVE_SAUDACAO = {
            "oi", "olá", "opa", "e aí", "tudo bem", "opa", "hi", "hey"
    };

    public static final int TIMEOUT_MINUTOS = 30;
}

