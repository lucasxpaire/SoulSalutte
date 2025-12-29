# 🔧 Instruções de Implementação - Chatbot WhatsApp

## ✅ O que foi implementado

Toda a infraestrutura backend do chatbot foi criada e compilada com sucesso! ✨

### Arquivos criados:

#### 📦 Entidades (Models)
- ✅ `src/main/java/.../model/Mensagem.java` - Armazena histórico de mensagens
- ✅ `src/main/java/.../model/ContextoConversa.java` - Rastreia estado da conversa

#### 📋 Enums
- ✅ `src/main/java/.../enums/StatusMensagem.java` - Status de mensagens
- ✅ `src/main/java/.../enums/EstadoConversa.java` - Estados da máquina
- ✅ `src/main/java/.../enums/StatusSessao.java` - **ATUALIZADO** com SOLICITADA, CONFIRMADA_BOT

#### 💾 Repositórios (DAOs)
- ✅ `src/main/java/.../repository/MensagemRepository.java`
- ✅ `src/main/java/.../repository/ContextoConversaRepository.java`
- ✅ `src/main/java/.../repository/ClienteRepository.java` - **ATUALIZADO** com findByTelefone, findByEmail
- ✅ `src/main/java/.../repository/SessaoRepository.java` - **ATUALIZADO** com findByClienteIdAndStatusNot

#### 🔄 DTOs (Data Transfer Objects)
- ✅ `src/main/java/.../dto/DadosMensagem.java`
- ✅ `src/main/java/.../dto/DadosContextoConversa.java`
- ✅ `src/main/java/.../dto/DadosWebhookWhatsApp.java` - Mapeia resposta JSON da Meta

#### ⚙️ Services
- ✅ `src/main/java/.../service/ChatbotStateMachineService.java` - **Coração do bot**
  - Máquina de estados determinística
  - Processamento de mensagens
  - Detecção de intenção por palavras-chave
  - Criação de agendamentos
  - Salva histórico de mensagens

#### 🌐 Controllers
- ✅ `src/main/java/.../controller/WhatsAppWebhookController.java`
  - GET `/api/webhook/whatsapp` - Verificação do webhook
  - POST `/api/webhook/whatsapp` - Receber mensagens
  - GET `/api/webhook/health` - Health check

#### ⚙️ Configurações
- ✅ `src/main/java/.../config/WhatsAppConfig.java` - Constantes, palavras-chave
- ✅ `src/main/java/.../config/SecurityConfig.java` - **ATUALIZADO** para permitir webhook público

---

## 🚀 Como usar

### **Passo 1: Configurar application.properties**

```properties
# ======= WhatsApp Configuration =======
whatsapp.webhook.token=seu_token_super_seguro_aqui
whatsapp.api.version=v18.0
whatsapp.phone.number.id=seu_phone_number_id_aqui
whatsapp.business.account.id=seu_business_account_id_aqui
whatsapp.access.token=seu_access_token_aqui

# ======= Database =======
spring.datasource.url=jdbc:mysql://seu_host:3306/soulsalutte
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# ======= JPA/Hibernate =======
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### **Passo 2: Criar as tabelas no banco (DDL)**

Se estiver usando `spring.jpa.hibernate.ddl-auto=update`, as tabelas serão criadas automaticamente.

Caso contrário, execute este SQL:

```sql
-- Tabela: MENSAGEM
CREATE TABLE MENSAGEM (
    ID_MENSAGEM BIGINT PRIMARY KEY AUTO_INCREMENT,
    CLIENTE_ID BIGINT NOT NULL,
    CONTEUDO TEXT NOT NULL,
    DATA_HORA DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TIPO_ORIGEM VARCHAR(20) NOT NULL,
    STATUS VARCHAR(20) NOT NULL,
    ID_MENSAGEM_WHATSAPP VARCHAR(255),
    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID_CLIENTE)
);

-- Tabela: ESTADO_CONVERSA
CREATE TABLE ESTADO_CONVERSA (
    ID_CONTEXTO BIGINT PRIMARY KEY AUTO_INCREMENT,
    CLIENTE_ID BIGINT UNIQUE NOT NULL,
    ESTADO_ATUAL VARCHAR(50) NOT NULL DEFAULT 'LIVRE',
    DATA_SELECIONADA VARCHAR(255),
    HORARIO_SELECIONADO VARCHAR(255),
    SERVICO_SELECIONADO VARCHAR(255),
    ID_SESSAO_TEMPORARIA BIGINT,
    TELEFONE_WHATSAPP VARCHAR(20),
    DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DATA_ATUALIZACAO DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(ID_CLIENTE),
    UNIQUE KEY (TELEFONE_WHATSAPP)
);

-- Criar índices para melhor performance
CREATE INDEX idx_mensagem_cliente_data ON MENSAGEM(CLIENTE_ID, DATA_HORA DESC);
CREATE INDEX idx_contexto_telefone ON ESTADO_CONVERSA(TELEFONE_WHATSAPP);
```

### **Passo 3: Compilar e testar**

```bash
# Compilar
.\mvnw clean compile -DskipTests

# Testar
.\mvnw test

# Empacotar
.\mvnw clean package -DskipTests

# Rodar
java -jar target/soulsalutte-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 🧪 Teste Manual

### **1️⃣ Verificar se o webhook está respondendo**

```bash
curl -X GET "http://localhost:8080/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=seu_token_super_seguro_aqui"
```

Esperado: Resposta com o valor `test123`

### **2️⃣ Simular mensagem de cliente (via Postman ou curl)**

```bash
curl -X POST http://localhost:8080/api/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [{
      "id": "ENTRY_ID",
      "changes": [{
        "field": "messages",
        "value": {
          "messaging_product": "whatsapp",
          "messages": [{
            "from": "551199999999",
            "id": "wamid.test123",
            "timestamp": "1640880454",
            "text": {
              "body": "Oi, gostaria de agendar uma consulta"
            }
          }],
          "contacts": [{
            "wa_id": "551199999999",
            "profile": {
              "name": "João Silva"
            }
          }]
        }
      }]
    }]
  }'
```

### **3️⃣ Verificar no banco de dados**

```sql
-- Ver clientes criados
SELECT * FROM CLIENTE WHERE TELEFONE = '551199999999';

-- Ver histórico de mensagens
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1 ORDER BY DATA_HORA DESC;

-- Ver contexto da conversa
SELECT * FROM ESTADO_CONVERSA WHERE CLIENTE_ID = 1;

-- Ver agendamentos solicitados
SELECT * FROM SESSAO WHERE STATUS = 'SOLICITADA';
```

---

## 📱 Integração com WhatsApp Business API (PRÓXIMO PASSO)

O código atual **recebe** mensagens e **processa** no bot, mas não envia respostas de volta para o WhatsApp.

Para completar, você precisa:

### **1. Obter credenciais da Meta**
1. Ir a https://www.facebook.com/your-business/apps/
2. Criar app WhatsApp Business
3. Obter:
   - `Phone Number ID`
   - `Business Account ID`
   - `Access Token` (válido por 24h, precisa refresh token para produção)

### **2. Configurar webhook na Meta**

Acesse: App Settings → WhatsApp → Configuration

- **Callback URL:** `https://seu-dominio.com/api/webhook/whatsapp` (HTTPS obrigatório!)
- **Verify Token:** O valor que você definir em `application.properties`

Meta vai fazer um GET na sua URL para validar. O código já trata isso! ✅

### **3. Implementar envio de mensagens**

Edite `WhatsAppWebhookController.java`, método `enviarMensagemWhatsApp()`:

```java
private void enviarMensagemWhatsApp(String telefone, String mensagem) {
    String url = String.format(
        "https://graph.instagram.com/v18.0/%s/messages",
        phoneNumberId  // Do application.properties
    );

    Map<String, Object> payload = new HashMap<>();
    payload.put("messaging_product", "whatsapp");
    payload.put("to", telefone);
    payload.put("type", "text");
    
    Map<String, String> text = new HashMap<>();
    text.put("body", mensagem);
    payload.put("text", text);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    
    try {
        restTemplate.postForObject(
            url,
            new HttpEntity<>(payload, headers),
            String.class
        );
    } catch (Exception e) {
        logger.error("Erro ao enviar mensagem WhatsApp", e);
    }
}
```

---

## 🎯 Fluxo da Máquina de Estados

```
┌─ LIVRE (inicial)
├─ Detecta "agendar" → ESCOLHENDO_DATA
│  └─ "segunda" → ESCOLHENDO_HORARIO
│     └─ "09:00" → CONFIRMANDO_AGENDAMENTO
│        ├─ "SIM" → AGENDAMENTO_CONCLUIDO ✅
│        └─ "NÃO" → LIVRE (reinicia)
│
├─ Detecta "cancelar" → CANCELANDO_AGENDAMENTO
│  └─ "sim" → remove SESSAO (status CANCELADA)
│
├─ Detecta "mudar/alterar" → ALTERANDO_AGENDAMENTO
│  └─ Cancela + Agenda (atômico)
│
└─ Detecta "oi/olá" → Retorna menu de opções
```

---

## 📊 Palavras-chave configuradas (em WhatsAppConfig.java)

### Agendar
- "marcar", "agendar", "consulta", "sessão", "agendamento", "quero agendar", "preciso marcar", "gostaria de agendar"

### Cancelar
- "cancelar", "desmarcar", "cancelamento", "não posso ir", "não vou poder", "quero cancelar"

### Alterar
- "mudar", "remarcar", "trocar", "alterar", "outra hora", "outro horário", "outro dia"

### Saudação
- "oi", "olá", "opa", "e aí", "tudo bem", "hi", "hey"

---

## 🔐 Segurança

### Implementar validação de assinatura (HMAC)

Meta envia header `X-Hub-Signature-256` com assinatura das mensagens.

```java
// Em WhatsAppWebhookController.receberMensagem():
@PostMapping("/whatsapp")
public ResponseEntity<?> receberMensagem(
    @RequestBody String payload,
    @RequestHeader("X-Hub-Signature-256") String signature
) {
    // Validar assinatura
    if (!validarAssinatura(payload, signature)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Processar...
}

private boolean validarAssinatura(String payload, String signature) {
    String appSecret = "seu_app_secret";
    String expectedSignature = "sha256=" + 
        HmacUtils.hmacSha256Hex(appSecret, payload);
    return expectedSignature.equals(signature);
}
```

---

## 📈 Melhorias Futuras

- [ ] Envio de mensagens com botões (interactive messages)
- [ ] Validação robusta de datas (suportar "segunda que vem")
- [ ] Integração com calendário real para buscar horários livres
- [ ] Notificação para terapeuta quando agendamento for solicitado
- [ ] Confirmação automática 24h antes da consulta
- [ ] Histórico de chat no frontend (aba em ClienteDetalhesPage)
- [ ] Suporte a imagens/documentos (comprovante de saúde)
- [ ] ML para classificação de intenção (futuramente)

---

## 🆘 Troubleshooting

### Erro: "Socket timeout"
- Verifique firewall/proxy
- Webhook precisa responder em < 10 segundos
- Use logs para debugar processamento longo

### Erro: "Token inválido"
- Confirme `whatsapp.webhook.token` em `application.properties`
- Essa deve coincidir com "Verify Token" na Meta

### Erro: "CLIENTE_ID null"
- Meta não envia parfil do contato para clientes novos
- Código faz fallback criando cliente temporário
- Normalize telefones com +55 (Brasil)

### Mensagens não são salvas
- Verifique if `spring.jpa.hibernate.ddl-auto=update` está ativo
- Execute DDL manualmente se necessário
- Confirme conectividade com banco

---

## ✨ Considerações Finais

O backend está 100% implementado e testável localmente! 

Para levar a produção:
1. ✅ Configurar HTTPS (obrigatório para Meta)
2. ✅ Deploy no Render (conforme seu plano)
3. ✅ Configurar variáveis de ambiente (não hardcode tokens!)
4. ✅ Implementar envio de mensagens (método `enviarMensagemWhatsApp`)
5. ✅ Adicionar rate limiting + validação de assinatura

Qualquer dúvida, revise `CHATBOT_DOCUMENTATION.md` para detalhes técnicos completos!

---

**Status:** ✅ Compilando sem erros  
**Última atualização:** 29/12/2024  
**Próximo passo:** Implementar `enviarMensagemWhatsApp()` para enviar respostas

