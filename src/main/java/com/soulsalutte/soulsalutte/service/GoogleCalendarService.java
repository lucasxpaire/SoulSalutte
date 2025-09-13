package com.soulsalutte.soulsalutte.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.soulsalutte.soulsalutte.model.Sessao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Soul Salutte Sistema";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${google.calendar.id}")
    private String calendarId;

    @Value("${google.credentials.file.path}")
    private String credentialsFilePath;

    private Calendar getCalendarService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleCalendarService.class.getResourceAsStream(credentialsFilePath.replace("classpath:", "/"));
        if (in == null) {
            throw new RuntimeException("Arquivo de credenciais não encontrado: " + credentialsFilePath);
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void adicionarEvento(Sessao sessao) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary(sessao.getNome() + " - " + sessao.getCliente().getNome())
                    .setDescription("Notas da sessão: " + sessao.getNotasSessao())
                    .setLocation("Soul Saluttē - Centro de Saúde e Bem-Estar");

            DateTime startDateTime = new DateTime(sessao.getDataHoraInicio().atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toEpochMilli());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/Sao_Paulo");
            event.setStart(start);

            DateTime endDateTime = new DateTime(sessao.getDataHoraFim().atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toEpochMilli());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("America/Sao_Paulo");
            event.setEnd(end);

            service.events().insert(calendarId, event)
                    .setSendNotifications(true)
                    .execute();

            System.out.println("Evento criado com sucesso no Google Agenda.");

        } catch (Exception e) {
            System.err.println("Erro ao criar evento no Google Agenda: " + e.getMessage());
        }
    }

    public List<TimePeriod> getOcupadosDoDia(LocalDate dia) throws Exception {
        Calendar service = getCalendarService();

        ZonedDateTime inicioDoDia = ZonedDateTime.of(dia, LocalTime.of(8, 0), ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime fimDoDia = ZonedDateTime.of(dia, LocalTime.of(20, 0), ZoneId.of("America/Sao_Paulo"));

        FreeBusyRequest req = new FreeBusyRequest();
        req.setTimeMin(new DateTime(inicioDoDia.toInstant().toEpochMilli()));
        req.setTimeMax(new DateTime(fimDoDia.toInstant().toEpochMilli()));
        req.setTimeZone("America/Sao_Paulo");

        FreeBusyRequestItem item = new FreeBusyRequestItem();
        item.setId(calendarId);
        req.setItems(Collections.singletonList(item));

        FreeBusyResponse response = service.freebusy().query(req).execute();
        return response.getCalendars().get(calendarId).getBusy();
    }

}