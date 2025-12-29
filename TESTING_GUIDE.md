# 🧪 Guia Prático de Testes - Chatbot WhatsApp

## Preparação do Ambiente

### 1. Iniciar a Aplicação

```bash
cd C:\Users\lucas\Desktop\SoulSalutteBack
.\mvnw clean compile -DskipTests
java -jar target/soulsalutte-0.0.1-SNAPSHOT.jar

# OU, se quiser usar Maven wrapper:
.\mvnw spring-boot:run
```

Você deve ver:
```
SoulSalutteApplication started in X seconds (JVM running for X.XXXs)
```

### 2. Verificar Banco de Dados

Conecte ao seu MySQL/TiDB:

```sql
-- Listar tabelas criadas
SHOW TABLES;

-- Verificar se as entidades foram criadas
DESC CLIENTE;
DESC MENSAGEM;
DESC ESTADO_CONVERSA;
DESC SESSAO;
```

---

## Testes Funcionais

### ✅ Teste 1: Health Check

**Objetivo:** Verificar se a aplicação está rodando

```bash
curl -X GET http://localhost:8080/api/webhook/health
```

**Resposta esperada:**
```json
{
  "status": "UP",
  "service": "WhatsApp Webhook"
}
```

---

### ✅ Teste 2: Verificação de Webhook (Token Válido)

**Objetivo:** Simular Meta validando o webhook

```bash
curl -X GET "http://localhost:8080/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=seu_token_super_seguro_aqui"
```

**Resposta esperada:**
```
test123
```

**O quê verificar:**
- Status HTTP: 200
- Body: exatamente "test123"

---

### ✅ Teste 3: Verificação de Webhook (Token Inválido)

**Objetivo:** Verificar que tokens inválidos são rejeitados

```bash
curl -X GET "http://localhost:8080/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=token_errado"
```

**Resposta esperada:**
```
Status: 403 Forbidden
```

**O quê verificar:**
- Status HTTP: 403
- Body: vazio ou erro

---

### ✅ Teste 4: Receber Mensagem de Agendamento

**Objetivo:** Simular cliente enviando "Gostaria de agendar"

```bash
curl -X POST http://localhost:8080/api/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [
      {
        "id": "ENTRY_ID_1",
        "changes": [
          {
            "field": "messages",
            "value": {
              "messaging_product": "whatsapp",
              "messages": [
                {
                  "from": "551199999999",
                  "id": "wamid.agendar_001",
                  "timestamp": "1640880454",
                  "text": {
                    "body": "Oi, gostaria de agendar uma consulta"
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
  }'
```

**Resposta esperada:**
```
Status: 200 OK
Body: (vazio)
```

**O quê verificar no banco:**

```sql
-- Cliente deve ser criado
SELECT * FROM CLIENTE WHERE TELEFONE = '551199999999';
-- Esperado: 1 registro com NOME = 'João Silva'

-- Mensagem recebida deve ser salva
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1;
-- Esperado: 1 registro com TIPO_ORIGEM = 'CLIENTE', CONTEUDO = 'Oi, gostaria de agendar uma consulta'

-- Contexto deve ser criado e atualizado
SELECT * FROM ESTADO_CONVERSA WHERE CLIENTE_ID = 1;
-- Esperado: ESTADO_ATUAL = 'ESCOLHENDO_DATA'

-- Mensagem de resposta do sistema deve ser salva
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1 AND TIPO_ORIGEM = 'SISTEMA';
-- Esperado: 1 registro com conteúdo contendo lista de dias
```

---

### ✅ Teste 5: Cliente Escolhe Data

**Objetivo:** Simular cliente respondendo com um número de dia

```bash
curl -X POST http://localhost:8080/api/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [
      {
        "id": "ENTRY_ID_2",
        "changes": [
          {
            "field": "messages",
            "value": {
              "messaging_product": "whatsapp",
              "messages": [
                {
                  "from": "551199999999",
                  "id": "wamid.escolher_data_002",
                  "timestamp": "1640880455",
                  "text": {
                    "body": "2"
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
  }'
```

**Resposta esperada:**
```
Status: 200 OK
```

**O quê verificar no banco:**

```sql
-- Contexto deve ser atualizado para ESCOLHENDO_HORARIO
SELECT * FROM ESTADO_CONVERSA WHERE CLIENTE_ID = 1;
-- Esperado: ESTADO_ATUAL = 'ESCOLHENDO_HORARIO', DATA_SELECIONADA = '2'

-- Nova mensagem de sistema com horários
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1 AND TIPO_ORIGEM = 'SISTEMA' ORDER BY DATA_HORA DESC LIMIT 1;
-- Esperado: conteúdo com lista de horários (08:00, 09:00, etc)
```

---

### ✅ Teste 6: Cliente Escolhe Horário

**Objetivo:** Simular cliente escolhendo um horário

```bash
curl -X POST http://localhost:8080/api/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [
      {
        "id": "ENTRY_ID_3",
        "changes": [
          {
            "field": "messages",
            "value": {
              "messaging_product": "whatsapp",
              "messages": [
                {
                  "from": "551199999999",
                  "id": "wamid.escolher_horario_003",
                  "timestamp": "1640880456",
                  "text": {
                    "body": "3"
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
  }'
```

**O quê verificar no banco:**

```sql
-- Contexto deve ser atualizado para CONFIRMANDO_AGENDAMENTO
SELECT * FROM ESTADO_CONVERSA WHERE CLIENTE_ID = 1;
-- Esperado: ESTADO_ATUAL = 'CONFIRMANDO_AGENDAMENTO', 
--          HORARIO_SELECIONADO = '3'

-- Sistema envia resumo
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1 AND TIPO_ORIGEM = 'SISTEMA' ORDER BY DATA_HORA DESC LIMIT 1;
-- Esperado: conteúdo com resumo do agendamento
```

---

### ✅ Teste 7: Cliente Confirma Agendamento

**Objetivo:** Simular cliente confirmando com "SIM"

```bash
curl -X POST http://localhost:8080/api/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{
    "object": "whatsapp_business_account",
    "entry": [
      {
        "id": "ENTRY_ID_4",
        "changes": [
          {
            "field": "messages",
            "value": {
              "messaging_product": "whatsapp",
              "messages": [
                {
                  "from": "551199999999",
                  "id": "wamid.confirmar_004",
                  "timestamp": "1640880457",
                  "text": {
                    "body": "SIM"
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
  }'
```

**O quê verificar no banco:**

```sql
-- Contexto deve estar em AGENDAMENTO_CONCLUIDO
SELECT * FROM ESTADO_CONVERSA WHERE CLIENTE_ID = 1;
-- Esperado: ESTADO_ATUAL = 'AGENDAMENTO_CONCLUIDO'

-- NOVO AGENDAMENTO CRIADO!
SELECT * FROM SESSAO WHERE CLIENTE_ID = 1;
-- Esperado: 1 registro com STATUS = 'SOLICITADA', 
--          NOME = 'Agendamento via WhatsApp'

-- Sistema confirma
SELECT * FROM MENSAGEM WHERE CLIENTE_ID = 1 AND TIPO_ORIGEM = 'SISTEMA' ORDER BY DATA_HORA DESC LIMIT 1;
-- Esperado: "Seu agendamento foi solicitado com sucesso! ✅"
```

---

## 🔍 Queries SQL Úteis para Verificação

### Ver todos os clientes criados
```sql
SELECT ID_CLIENTE, NOME, EMAIL, TELEFONE FROM CLIENTE;
```

### Ver histórico completo de conversa
```sql
SELECT CLIENTE_ID, CONTEUDO, DATA_HORA, TIPO_ORIGEM, STATUS 
FROM MENSAGEM 
WHERE CLIENTE_ID = 1
ORDER BY DATA_HORA ASC;
```

### Ver estado atual da conversa
```sql
SELECT CLIENTE_ID, ESTADO_ATUAL, DATA_SELECIONADA, HORARIO_SELECIONADO, DATA_ATUALIZACAO
FROM ESTADO_CONVERSA
WHERE CLIENTE_ID = 1;
```

### Ver agendamentos solicitados (pendentes de aprovação)
```sql
SELECT ID_SESSAO, CLIENTE_ID, NOME, DATA_HORA_INICIO, DATA_HORA_FIM, STATUS
FROM SESSAO
WHERE STATUS = 'SOLICITADA'
ORDER BY DATA_HORA_INICIO DESC;
```

### Ver todos os agendamentos de um cliente
```sql
SELECT ID_SESSAO, NOME, DATA_HORA_INICIO, STATUS
FROM SESSAO
WHERE CLIENTE_ID = 1
ORDER BY DATA_HORA_INICIO DESC;
```

### Contar mensagens por tipo de origem
```sql
SELECT CLIENTE_ID, TIPO_ORIGEM, COUNT(*) as TOTAL
FROM MENSAGEM
GROUP BY CLIENTE_ID, TIPO_ORIGEM;
```

---

## 📊 Exemplo de Teste Completo (Fluxo)

### Cenário: Novo cliente quer agendar uma consulta

1. **Começar com banco vazio**
   ```sql
   DELETE FROM MENSAGEM;
   DELETE FROM ESTADO_CONVERSA;
   DELETE FROM SESSAO;
   DELETE FROM CLIENTE;
   ```

2. **Executar testes 1-7 em sequência** (cada um chama a API)

3. **Verificar resultados finais**
   ```sql
   -- Deve ter 1 cliente
   SELECT COUNT(*) FROM CLIENTE; -- 1
   
   -- Deve ter 7 mensagens (4 do cliente, 3 do sistema)
   SELECT COUNT(*) FROM MENSAGEM; -- 7
   SELECT COUNT(*) FROM MENSAGEM WHERE TIPO_ORIGEM = 'CLIENTE'; -- 4
   SELECT COUNT(*) FROM MENSAGEM WHERE TIPO_ORIGEM = 'SISTEMA'; -- 3
   
   -- Deve ter 1 contexto em AGENDAMENTO_CONCLUIDO
   SELECT COUNT(*) FROM ESTADO_CONVERSA; -- 1
   
   -- Deve ter 1 sessão SOLICITADA
   SELECT COUNT(*) FROM SESSAO; -- 1
   SELECT STATUS FROM SESSAO; -- SOLICITADA
   ```

---

## 🆘 Troubleshooting

### Erro: "404 Not Found"
- Verifique se a aplicação está rodando
- Verifique a porta (padrão 8080)
- Verifique o caminho da URL

### Erro: "500 Internal Server Error"
- Verifique os logs da aplicação
- Pode ser erro de banco de dados
- Verifique se as tabelas foram criadas

### Tabelas não criadas
- Verifique `application.properties`: `spring.jpa.hibernate.ddl-auto=update`
- Ou execute manualmente o DDL (veja CHATBOT_SETUP.md)

### Cliente não é criado
- Verifique email gerado: `whatsapp_<telefone>@temp.local`
- Pode haver constraint de email duplicado
- Execute: `DELETE FROM CLIENTE WHERE EMAIL LIKE '%@temp.local%'`

### Mensagens não são salvas
- Verifique se Cliente foi criado (FK obrigatória)
- Verifique transação não foi rolada por erro
- Veja logs: `grep "Erro ao processar" application.log`

---

## 📈 Performance Testing

### Teste de múltiplos clientes

```bash
#!/bin/bash
# Enviar 10 mensagens de clientes diferentes

for i in {1..10}; do
  PHONE="5511999999${i}0"
  curl -X POST http://localhost:8080/api/webhook/whatsapp \
    -H "Content-Type: application/json" \
    -d "{
      \"object\": \"whatsapp_business_account\",
      \"entry\": [{
        \"id\": \"ENTRY_$i\",
        \"changes\": [{
          \"field\": \"messages\",
          \"value\": {
            \"messages\": [{
              \"from\": \"$PHONE\",
              \"text\": { \"body\": \"Oi, quero agendar\" }
            }],
            \"contacts\": [{
              \"wa_id\": \"$PHONE\",
              \"profile\": { \"name\": \"Cliente $i\" }
            }]
          }
        }]
      }]
    }"
  
  echo "Cliente $i enviado"
  sleep 0.5
done

echo "✅ 10 clientes enviados"
```

---

## ✅ Checklist de Testes

Antes de colocar em produção, execute:

- [ ] Teste 1: Health check
- [ ] Teste 2: Webhook verification (válido)
- [ ] Teste 3: Webhook verification (inválido)
- [ ] Teste 4: Receber mensagem
- [ ] Teste 5: Escolher data
- [ ] Teste 6: Escolher horário
- [ ] Teste 7: Confirmar agendamento
- [ ] Teste de múltiplos clientes
- [ ] Verificar banco de dados após cada teste
- [ ] Verificar logs para erros
- [ ] Limpar dados de teste antes de produção

---

**Última atualização:** 29/12/2024  
**Versão:** 1.0  
**Status:** ✅ Pronto para teste

