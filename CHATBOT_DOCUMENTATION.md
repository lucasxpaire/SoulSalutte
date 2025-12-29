# 🤖 Documentação - Chatbot WhatsApp SoulSalutte

## 📋 Visão Geral

Este documento descreve a arquitetura e implementação do **Chatbot WhatsApp** integrado ao backend SoulSalutte. O sistema utiliza uma **máquina de estados determinística** para gerenciar conversas com clientes e automatizar fluxos de agendamento.

---

## 🏗️ Arquitetura

### Componentes Principais

```
┌─────────────────────────────────────────────────────────┐
│         WhatsApp Business API (Meta)                    │
└────────────────────┬────────────────────────────────────┘
                     │ POST /api/webhook/whatsapp
                     ▼
┌─────────────────────────────────────────────────────────┐
│    WhatsAppWebhookController                            │
│ - Valida token de webhook                              │
│ - Recebe mensagens da Meta                             │
│ - Extrai dados de contato e mensagem                   │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│    ChatbotStateMachineService                           │
│ - Processa mensagens                                    │
│ - Gerencia estados de conversa (máquina de estados)    │
│ - Cria/altera agendamentos                             │
│ - Salva histórico de mensagens                         │
└────────────────────┬────────────────────────────────────┘
                     │
          ┌──────────┼──────────┬────────────┐
          ▼          ▼          ▼            ▼
     ┌─────────┬──────────┬──────────┬───────────┐
     │ Cliente │ Sessao   │ Mensagem │ Contexto  │
     │ Repo    │ Repo     │ Repo     │ Conversa  │
     │         │          │          │ Repo      │
     └─────────┴──────────┴──────────┴───────────┘
```

---

## 🔄 Máquina de Estados

A conversa segue um fluxo de estados predefinidos:

```
LIVRE (estado inicial)
  ├─ Palavras: "agendar", "marcar" → ESCOLHENDO_DATA
  ├─ Palavras: "cancelar" → CANCELANDO_AGENDAMENTO
  ├─ Palavras: "mudar", "alterar" → ALTERANDO_AGENDAMENTO
  └─ Palavras: "oi", "olá" → Retorna menu de opções

ESCOLHENDO_DATA
  └─ Cliente seleciona dia → ESCOLHENDO_HORARIO

ESCOLHENDO_HORARIO
  └─ Cliente seleciona horário → CONFIRMANDO_AGENDAMENTO

CONFIRMANDO_AGENDAMENTO
  ├─ Cliente confirma (SIM) → AGENDAMENTO_CONCLUIDO
  └─ Cliente recusa (NÃO) → LIVRE

AGENDAMENTO_CONCLUIDO
  └─ Retorna para LIVRE (após confirmação manual)
```

---

## 📁 Entidades do Banco

### 1. **Mensagem**
Armazena histórico de mensagens entre cliente e sistema.

```sql
CREATE TABLE MENSAGEM (
    ID_MENSAGEM BIGINT PRIMARY KEY,
    CLIENTE_ID BIGINT,
    CONTEUDO TEXT,
    DATA_HORA DATETIME,
    TIPO_ORIGEM VARCHAR (20),  -- CLIENTE ou SISTEMA
    STATUS VARCHAR (20),        -- ENVIADA, ENTREGUE, LIDA, ERRO
    ID_MENSAGEM_WHATSAPP VARCHAR (255),
    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID_CLIENTE)
);
```

### 2. **ContextoConversa** (EstadoConversa)
Rastreia o contexto e estado atual da conversa com cada cliente.

```sql
CREATE TABLE ESTADO_CONVERSA (
    ID_CONTEXTO BIGINT PRIMARY KEY,
    CLIENTE_ID BIGINT UNIQUE,
    ESTADO_ATUAL VARCHAR (50),   -- Enum EstadoConversa
    DATA_SELECIONADA VARCHAR (255),
    HORARIO_SELECIONADO VARCHAR (255),
    SERVICO_SELECIONADO VARCHAR (255),
    ID_SESSAO_TEMPORARIA BIGINT,
    TELEFONE_WHATSAPP VARCHAR (20),
    DATA_CRIACAO DATETIME,
    DATA_ATUALIZACAO DATETIME,
    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID_CLIENTE)
);
```

### 3. **Sessao** (Modificada)
Agora suporta novos status para agendamentos via bot.

```java
StatusSessao {
    SOLICITADA,        // Bot criou, aguarda aprovação
    AGENDADA,          // Confirmada manualmente
    CONFIRMADA_BOT,    // Confirmada pelo bot (futuro)
    CONCLUIDA,         // Sessão realizada
    CANCELADA          // Cancelada
}
```

---

## 🔌 Endpoints

### **GET /api/webhook/whatsapp** (Verificação)
Endpoint para Meta validar o webhook.

**Parâmetros Query:**
- `hub.mode` = "subscribe"
- `hub.challenge` = token de desafio
- `hub.verify_token` = seu token configurado

**Resposta:** 200 OK com o token de desafio

### **POST /api/webhook/whatsapp** (Receber Mensagens)
Recebe mensagens de clientes via WhatsApp.

**Body (exemplo):**
```json
{
  "object": "whatsapp_business_account",
  "entry": [
    {
      "id": "...",
      "changes": [
        {
          "field": "messages",
          "value": {
            "messaging_product": "whatsapp",
            "messages": [
              {
                "from": "551199999999",
                "id": "wamid.xxx",
                "timestamp": "1640880454",
                "text": {
                  "body": "Olá, gostaria de agendar"
                }
              }
            ],
            "contacts": [
              {
                "wa_id": "551199999999",
                "profile": {
                  "name": "João Silva"
                }
              }
            ]
          }
        }
      ]
    }
  ]
}
```

**Resposta:** 200 OK (sempre, para evitar retentativas da Meta)

---

## ⚙️ Configuração

### 1. **application.properties**

```properties
# WhatsApp Configuration
whatsapp.webhook.token=seu_token_seguro_aqui
whatsapp.api.version=v18.0
whatsapp.phone.number.id=seu_phone_number_id
whatsapp.business.account.id=seu_business_account_id
whatsapp.access.token=seu_access_token_meta
```

### 2. **SecurityConfig** (Atualizado)
O projeto foi atualizado para permitir acesso ao webhook sem autenticação JWT:

```java
.authorizeHttpRequests(req -> {
    req.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
    req.requestMatchers(HttpMethod.GET, "/api/webhook/**").permitAll();    // ← NOVO
    req.requestMatchers(HttpMethod.POST, "/api/webhook/**").permitAll();   // ← NOVO
    req.anyRequest().authenticated();
})
```

---

## 🔑 Fluxo Completo de Agendamento

### 1️⃣ **Cliente envia mensagem**
```
Cliente: "Olá, gostaria de agendar uma consulta"
```

### 2️⃣ **Sistema processa**
- Valida webhook da Meta
- Busca/cria Cliente pelo telefone
- Busca/cria ContextoConversa
- Detecta palavra-chave: "agendar"
- Muda estado para `ESCOLHENDO_DATA`

### 3️⃣ **Bot responde**
```
Bot: "Qual dia você prefere?
1️⃣ Segunda
2️⃣ Terça
...
```

### 4️⃣ **Cliente escolhe data**
```
Cliente: "1"  (Segunda)
```

### 5️⃣ **Bot lista horários**
```
Bot: "Qual horário você prefere?
1️⃣ 08:00
2️⃣ 09:00
...
```

### 6️⃣ **Cliente escolhe horário**
```
Cliente: "2"  (09:00)
```

### 7️⃣ **Bot confirma**
```
Bot: "Resumo do seu agendamento:
📅 Data: Segunda
⏰ Horário: 09:00
Confirma? Digite SIM ou NÃO."
```

### 8️⃣ **Cliente confirma**
```
Cliente: "SIM"
```

### 9️⃣ **Sistema cria agendamento**
- Cria `Sessao` com `status = SOLICITADA`
- Muda estado para `AGENDAMENTO_CONCLUIDO`
- Salva contexto

### 🔟 **Bot avisa**
```
Bot: "Seu agendamento foi solicitado com sucesso! ✅
Você receberá uma confirmação em breve."
```

---

## 📝 Classes Criadas

### Entidades
- `Mensagem.java` - Histórico de mensagens
- `ContextoConversa.java` - Contexto da conversa

### Enums
- `StatusMensagem.java` - Status da mensagem (ENVIADA, ENTREGUE, LIDA, ERRO)
- `EstadoConversa.java` - Estados da máquina
- `StatusSessao.java` - **Atualizado** com SOLICITADA e CONFIRMADA_BOT

### Repositórios
- `MensagemRepository.java`
- `ContextoConversaRepository.java`

### DTOs
- `DadosMensagem.java`
- `DadosContextoConversa.java`
- `DadosWebhookWhatsApp.java`

### Services
- `ChatbotStateMachineService.java` - Lógica da máquina de estados

### Controllers
- `WhatsAppWebhookController.java` - Endpoints do webhook

### Configurações
- `WhatsAppConfig.java` - Constantes e palavras-chave
- `SecurityConfig.java` - **Atualizado** para permitir webhook

---

## 🚀 Próximos Passos

### 1. **Integração com WhatsApp Business API**
Atualmente, o bot recebe mensagens e salva respostas. Precisa implementar:

```java
// Em WhatsAppWebhookController.enviarMensagemWhatsApp():
private void enviarMensagemWhatsApp(String telefone, String mensagem) {
    String url = "https://graph.instagram.com/v18.0/{phone_number_id}/messages";
    // Usar RestTemplate ou WebClient para enviar
    // POST com JSON: { "messaging_product": "whatsapp", "to": telefone, "type": "text", "text": { "body": mensagem } }
}
```

### 2. **Aprimorar Parse de Data/Hora**
- Implementar parser robusto para datas (ex: "segunda que vem", "15/01")
- Validar horários contra disponibilidade real do terapeuta

### 3. **Suporte a Reações/Emojis**
Meta permite mensagens com botões/listas. Melhorar UX com:

```java
// Enviar mensagem com botões em vez de texto simples
{
  "messaging_product": "whatsapp",
  "to": "551199999999",
  "type": "interactive",
  "interactive": {
    "type": "button",
    "body": { "text": "Escolha um dia:" },
    "action": {
      "buttons": [
        { "type": "reply", "reply": { "id": "1", "title": "Segunda" } },
        { "type": "reply", "reply": { "id": "2", "title": "Terça" } }
      ]
    }
  }
}
```

### 4. **Frontend Integration**
No React, atualizar:
- `CalendarioPage` - Diferenciar cores: SOLICITADA (🟡), AGENDADA (🟢)
- `ClienteDetalhesPage` - Aba "Histórico de Chat" com mensagens salvas

### 5. **Notificações**
- Enviar confirmação por email/SMS quando admin aprovar agendamento
- Notificar cliente 24h antes da consulta

---

## 🧪 Testes

### Teste Manual com Postman

**1. Verificar Webhook:**
```
GET http://localhost:8080/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=seuTokenAqui
```

**2. Simular Mensagem:**
```
POST http://localhost:8080/api/webhook/whatsapp
Content-Type: application/json

{
  "object": "whatsapp_business_account",
  "entry": [{
    "changes": [{
      "value": {
        "messages": [{
          "from": "551199999999",
          "text": { "body": "Oi, quero agendar" }
        }],
        "contacts": [{
          "wa_id": "551199999999",
          "profile": { "name": "João" }
        }]
      }
    }]
  }]
}
```

---

## 📊 Exemplo de Banco de Dados

```sql
-- Cliente criado automaticamente pelo bot
INSERT INTO CLIENTE (NOME, EMAIL, TELEFONE, DATA_NASCIMENTO, SEXO, CIDADE)
VALUES ('João Silva', 'whatsapp_551199999999@temp.local', '551199999999', '1990-01-01', 'M', 'São Paulo');

-- Histórico de mensagens
INSERT INTO MENSAGEM (CLIENTE_ID, CONTEUDO, DATA_HORA, TIPO_ORIGEM, STATUS)
VALUES (1, 'Oi, gostaria de agendar', NOW(), 'CLIENTE', 'ENTREGUE');
VALUES (1, 'Qual dia você prefere? 1️⃣ Segunda...', NOW(), 'SISTEMA', 'ENVIADA');

-- Contexto da conversa
INSERT INTO ESTADO_CONVERSA (CLIENTE_ID, ESTADO_ATUAL, DATA_SELECIONADA, HORARIO_SELECIONADO)
VALUES (1, 'CONFIRMANDO_AGENDAMENTO', 'Segunda', '09:00');

-- Sessão solicitada
INSERT INTO SESSAO (CLIENTE_ID, NOME, DATA_HORA_INICIO, DATA_HORA_FIM, STATUS)
VALUES (1, 'Agendamento via WhatsApp', '2024-01-08 09:00:00', '2024-01-08 10:00:00', 'SOLICITADA');
```

---

## ⚠️ Considerações de Segurança

1. **Token de Webhook** - Mude `seuTokenAqui` para um valor seguro
2. **Validação de Assinatura** - Meta envia `X-Hub-Signature-256` header (implementar validação HMAC)
3. **Rate Limiting** - Considere adicionar limites de mensagens por cliente
4. **LGPD Compliance** - Avisar cliente sobre armazenamento de dados

---

## 📚 Referências

- [WhatsApp Business API Docs](https://developers.facebook.com/docs/whatsapp/cloud-api/webhooks/components)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [JPA/Hibernate Docs](https://hibernate.org/orm/)

---

**Última atualização:** 29/12/2024
**Autor:** GitHub Copilot

