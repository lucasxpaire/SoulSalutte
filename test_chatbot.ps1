# Script de teste para o Chatbot WhatsApp (PowerShell - Windows)
# Execute: powershell -ExecutionPolicy Bypass -File test_chatbot.ps1

Write-Host "🧪 Testes de Chatbot WhatsApp - SoulSalutte" -ForegroundColor Yellow
Write-Host "=================================================" -ForegroundColor Yellow
Write-Host ""

# Configuração
$SERVER = "http://localhost:8080"
$WEBHOOK_TOKEN = "seu_token_super_seguro_aqui"

# Test 1: Health Check
Write-Host "[1/4] Testando health check..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$SERVER/api/webhook/health" -Method Get
    Write-Host "✅ Health Check OK" -ForegroundColor Green
    $response | ConvertTo-Json | Write-Host
} catch {
    Write-Host "❌ Erro no health check: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: Webhook Verification (Valid)
Write-Host "[2/4] Testando verificação de webhook (válido)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod `
        -Uri "$SERVER/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=$WEBHOOK_TOKEN" `
        -Method Get
    if ($response -eq "test123") {
        Write-Host "✅ Webhook validado com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "❌ Resposta inesperada: $response" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Erro: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Webhook Verification (Invalid Token)
Write-Host "[3/4] Testando verificação de webhook (token inválido)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod `
        -Uri "$SERVER/api/webhook/whatsapp?hub.mode=subscribe&hub.challenge=test123&hub.verify_token=token_errado" `
        -Method Get `
        -ErrorAction SilentlyContinue
    Write-Host "❌ Deveria ter retornado erro 403" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode.Value__ -eq 403) {
        Write-Host "✅ Corretamente rejeitado (403)" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro inesperado: $_" -ForegroundColor Red
    }
}
Write-Host ""

# Test 4: Receive Message
Write-Host "[4/4] Testando recebimento de mensagem..." -ForegroundColor Yellow

$payload = @{
    object = "whatsapp_business_account"
    entry = @(
        @{
            id = "ENTRY_ID_123"
            changes = @(
                @{
                    field = "messages"
                    value = @{
                        messaging_product = "whatsapp"
                        messages = @(
                            @{
                                from = "551199999999"
                                id = "wamid.test_agendar_123"
                                timestamp = "1640880454"
                                text = @{
                                    body = "Oi, gostaria de agendar uma consulta"
                                }
                            }
                        )
                        contacts = @(
                            @{
                                wa_id = "551199999999"
                                profile = @{
                                    name = "João Silva"
                                }
                            }
                        )
                    }
                }
            )
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod `
        -Uri "$SERVER/api/webhook/whatsapp" `
        -Method Post `
        -Headers @{"Content-Type" = "application/json"} `
        -Body $payload
    Write-Host "✅ Mensagem processada com sucesso!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro ao processar mensagem: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "=================================================" -ForegroundColor Yellow
Write-Host "🎉 Testes concluídos!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Próximos passos:" -ForegroundColor Cyan
Write-Host "1. Verificar se o cliente foi criado no banco:" -ForegroundColor White
Write-Host "   SELECT * FROM CLIENTE WHERE TELEFONE = '551199999999';" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Verificar mensagens salvas:" -ForegroundColor White
Write-Host "   SELECT * FROM MENSAGEM ORDER BY DATA_HORA DESC LIMIT 5;" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Verificar contexto da conversa:" -ForegroundColor White
Write-Host "   SELECT * FROM ESTADO_CONVERSA WHERE TELEFONE_WHATSAPP = '551199999999';" -ForegroundColor Gray
Write-Host ""

