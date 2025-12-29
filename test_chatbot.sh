#!/bin/bash
echo ""
echo "   SELECT * FROM ESTADO_CONVERSA WHERE TELEFONE_WHATSAPP = '551199999999';"
echo "3. Verificar contexto da conversa:"
echo ""
echo "   SELECT * FROM MENSAGEM ORDER BY DATA_HORA DESC LIMIT 5;"
echo "2. Verificar mensagens salvas:"
echo ""
echo "   SELECT * FROM CLIENTE WHERE TELEFONE = '551199999999';"
echo "1. Verificar se o cliente foi criado no banco:"
echo "📝 Próximos passos:"
echo ""
echo -e "${GREEN}🎉 Testes concluídos!${NC}"
echo "=================================================="

echo ""
fi
    echo "Resposta: $RESPONSE"
else
    echo -e "${GREEN}✅ Mensagem processada com sucesso (resposta vazia = 200 OK)${NC}"
if [ "$RESPONSE" = "" ]; then

  -d "$PAYLOAD")
  -H "Content-Type: application/json" \
RESPONSE=$(curl -s -X POST "$SERVER/api/webhook/whatsapp" \

)
EOF
}
  ]
    }
      ]
        }
          }
            ]
              }
                }
                  "name": "João Silva"
                "profile": {
                "wa_id": "551199999999",
              {
            "contacts": [
            ],
              }
                }
                  "body": "Oi, gostaria de agendar uma consulta"
                "text": {
                "timestamp": "1640880454",
                "id": "wamid.test_agendar_123",
                "from": "551199999999",
              {
            "messages": [
            "messaging_product": "whatsapp",
          "value": {
          "field": "messages",
        {
      "changes": [
      "id": "ENTRY_ID_123",
    {
  "entry": [
  "object": "whatsapp_business_account",
{
PAYLOAD=$(cat <<EOF
echo -e "${YELLOW}[4/4] Testando recebimento de mensagem (agendar)...${NC}"

echo ""
fi
    echo -e "${RED}❌ Deveria ter sido rejeitado${NC}"
else
    echo -e "${GREEN}✅ Corretamente rejeitado (403)${NC}"
if [ "$HTTP_CODE" = "403" ]; then
HTTP_CODE=$(echo "$RESPONSE" | tail -1)
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$SERVER/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=token_errado")
echo -e "${YELLOW}[3/4] Testando verificação de webhook (token inválido)...${NC}"

echo ""
fi
    echo "Resposta: $RESPONSE"
    echo -e "${RED}❌ Erro na validação do webhook${NC}"
else
    echo -e "${GREEN}✅ Webhook validado com sucesso!${NC}"
if [ "$RESPONSE" = "test123" ]; then
RESPONSE=$(curl -s -X GET "$SERVER/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=$WEBHOOK_TOKEN")
echo -e "${YELLOW}[2/4] Testando verificação de webhook (válido)...${NC}"

echo ""
curl -s -X GET "$SERVER/api/webhook/health" | jq '.' || echo "ERRO: Health check falhou"
echo -e "${YELLOW}[1/4] Testando health check...${NC}"

NC='\033[0m' # No Color
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
RED='\033[0;31m'
# Cores para output

WEBHOOK_TOKEN="seu_token_super_seguro_aqui"
SERVER="http://localhost:8080"
# Configuração

echo ""
echo "=================================================="
echo "🧪 Testes de Chatbot WhatsApp - SoulSalutte"

# Use PowerShell no Windows: powershell -ExecutionPolicy Bypass -File test_chatbot.ps1
# Script de teste para o Chatbot WhatsApp

