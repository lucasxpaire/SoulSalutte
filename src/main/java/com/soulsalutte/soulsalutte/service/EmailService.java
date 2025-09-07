package com.soulsalutte.soulsalutte.service;

import com.soulsalutte.soulsalutte.model.Sessao;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarNotificacaoAgendamento(Sessao sessao) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente, "Soul Saluttē");
            helper.setTo(sessao.getCliente().getEmail());
            helper.setSubject("✅ Confirmação de Agendamento - Soul Saluttē");

            String conteudoIcs = gerarConteudoIcs(sessao);

            String corpoHtml = construirCorpoEmail(sessao);
            helper.setText(corpoHtml, true);

            helper.addAttachment("invite.ics", new ByteArrayResource(conteudoIcs.getBytes(StandardCharsets.UTF_8)), "text/calendar");

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Erro ao enviar e-mail de confirmação: " + e.getMessage());
        }
    }

    private String gerarConteudoIcs(Sessao sessao) {
        ZoneId fusoHorarioBrasil = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime inicioComFuso = sessao.getDataHoraInicio().atZone(fusoHorarioBrasil);
        ZonedDateTime fimComFuso = sessao.getDataHoraFim().atZone(fusoHorarioBrasil);
        DateTimeFormatter googleFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        String dtstamp = ZonedDateTime.now(ZoneId.of("UTC")).format(googleFormatter);
        String dtstart = inicioComFuso.withZoneSameInstant(ZoneId.of("UTC")).format(googleFormatter);
        String dtend = fimComFuso.withZoneSameInstant(ZoneId.of("UTC")).format(googleFormatter);

        return String.join("\r\n",
                "BEGIN:VCALENDAR",
                "VERSION:2.0",
                "PRODID:-//SoulSalutte//Sessao de Fisioterapia//PT",
                "METHOD:REQUEST",
                "BEGIN:VEVENT",
                "UID:" + sessao.getId() + "@soulsalutte.com",
                "DTSTAMP:" + dtstamp,
                "DTSTART:" + dtstart,
                "DTEND:" + dtend,
                "SUMMARY:Sessão de Fisioterapia - Soul Saluttē",
                "DESCRIPTION:Sua sessão de fisioterapia com " + sessao.getCliente().getNome() + ". Por favor, chegue com alguns minutos de antecedência.",
                "LOCATION:Soul Saluttē - Centro de Saúde e Bem-Estar",
                "STATUS:CONFIRMED",
                "ORGANIZER;CN=Soul Saluttē:mailto:" + remetente,
                "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE;CN=" + sessao.getCliente().getNome() + ":mailto:" + sessao.getCliente().getEmail(),
                "BEGIN:VALARM",
                "TRIGGER:-PT1H",
                "ACTION:DISPLAY",
                "DESCRIPTION:Lembrete da Sessão",
                "END:VALARM",
                "END:VEVENT",
                "END:VCALENDAR"
        );
    }

    private String construirCorpoEmail(Sessao sessao) {
        Locale localePtBr = new Locale("pt", "BR");
        String nomeCliente = sessao.getCliente().getNome();
        String dataSessao = sessao.getDataHoraInicio().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", localePtBr));
        String horaInicio = sessao.getDataHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"));
        String horaFim = sessao.getDataHoraFim().format(DateTimeFormatter.ofPattern("HH:mm"));

        ZoneId fusoHorarioBrasil = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime inicioComFuso = sessao.getDataHoraInicio().atZone(fusoHorarioBrasil);
        ZonedDateTime fimComFuso = sessao.getDataHoraFim().atZone(fusoHorarioBrasil);

        DateTimeFormatter googleFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String googleDataInicio = inicioComFuso.withZoneSameInstant(ZoneId.of("UTC")).format(googleFormatter);
        String googleDataFim = fimComFuso.withZoneSameInstant(ZoneId.of("UTC")).format(googleFormatter);
        String tituloEvento = URLEncoder.encode("Sessão de Fisioterapia - Soul Saluttē", StandardCharsets.UTF_8);
        String detalhesEvento = URLEncoder.encode("Sua sessão de fisioterapia com Lauren Pairé. Por favor, chegue com alguns minutos de antecedência.", StandardCharsets.UTF_8);
        String localizacao = URLEncoder.encode("Soul Saluttē - Centro de Saúde e Bem-Estar", StandardCharsets.UTF_8);

        String googleCalendarUrl = String.format(
                "https://www.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%s/%s&details=%s&location=%s",
                tituloEvento,
                googleDataInicio,
                googleDataFim,
                detalhesEvento,
                localizacao
        );

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><style>"
                + "  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #333; }"
                + "  .container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 12px; background-color: #fdfbf7; }"
                + "  .header { background-color: #1A7B7D; color: #ffffff; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }"
                + "  .header h2 { margin: 0; font-size: 24px; }"
                + "  .content { padding: 25px; line-height: 1.6; text-align: center; }"
                + "  .details { background-color: #ffffff; border: 1px solid #eee; padding: 20px; border-radius: 8px; margin-top: 15px; margin-bottom: 20px; text-align: left; }"
                + "  .details p { margin: 10px 0; }"
                + "  .button-container { text-align: center; margin: 25px 0; }"
                + "  .calendar-button { display: inline-block; background-color: #4285F4; color: #ffffff; padding: 12px 20px; border-radius: 5px; text-decoration: none; font-weight: bold; }"
                + "  .footer { margin-top: 25px; font-size: 0.8em; text-align: center; color: #888; }"
                + "</style></head>"
                + "<body>"
                + "  <div class='container'>"
                + "    <div class='header'><h2>Confirmação de Agendamento</h2></div>"
                + "    <div class='content'>"
                + "      <p style='text-align: left;'>Olá, " + nomeCliente + "!</p>"
                + "      <p style='text-align: left;'>Sua sessão na <strong>Soul Saluttē</strong> foi confirmada. Confira os detalhes:</p>"
                + "      <div class='details'>"
                + "        <p><strong>Sessão:</strong> " + sessao.getNome() + "</p>"
                + "        <p>🗓️ <strong>Data:</strong> " + dataSessao + "</p>"
                + "        <p>⏰ <strong>Horário:</strong> " + horaInicio + " às " + horaFim + "</p>"
                + "      </div>"
                + "      <p>Para sua conveniência, enviamos um convite em anexo. <strong>Abra o anexo para adicionar o evento diretamente à agenda do seu celular ou computador.</strong></p>"
                + "      <div class='button-container'>"
                + "        <a href='" + googleCalendarUrl + "' class='calendar-button' target='_blank'>+ Adicionar à Agenda Google (Alternativa)</a>"
                + "      </div>"
                + "      <p style='text-align: left; margin-top: 20px;'>Se precisar reagendar, entre em contato conosco.</p>"
                + "      <p style='text-align: left;'>Atenciosamente,<br/>Equipe Soul Saluttē</p>"
                + "    </div>"
                + "    <div class='footer'>Soul Saluttē - Centro de Saúde e Bem-Estar</div>"
                + "  </div>"
                + "</body>"
                + "</html>";
    }
}