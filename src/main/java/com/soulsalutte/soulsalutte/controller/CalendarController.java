package com.soulsalutte.soulsalutte.controller;

import com.google.api.services.calendar.model.TimePeriod;
import com.soulsalutte.soulsalutte.service.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/disponibilidade")
    public ResponseEntity<?> getDisponibilidade(@RequestParam String dia) {
        try {
            LocalDate dataSelecionada = LocalDate.parse(dia);
            List<TimePeriod> periodosOcupados = googleCalendarService.getOcupadosDoDia(dataSelecionada);
            return ResponseEntity.ok(periodosOcupados);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao consultar a agenda: " + e.getMessage());
        }
    }
}