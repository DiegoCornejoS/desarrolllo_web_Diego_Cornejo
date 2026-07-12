package com.dcc.actividades.controller;

import com.dcc.actividades.model.LogEntry;
import com.dcc.actividades.repository.LogRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador para la funcionalidad 2 de la Tarea 5: Mensajes de Log.
 * 
 * Acceso restringido a los usuarios:
 *  - cc5002/examen (rol ADMIN)
 *  - auditor/log-auditor (rol AUDITOR)
 * 
 * Muestra el contenido completo de la tabla "log" ordenado del más reciente al más antiguo.
 */
@Controller
public class MensajesLogController {

    private final LogRepository logRepository;

    public MensajesLogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Muestra todos los registros de la tabla log (id, fecha, mensaje)
     * ordenados del más reciente al más antiguo.
     */
    @GetMapping("/mensajes-log")
    public String mensajesLog(Model model) {
        List<LogEntry> logs = logRepository.findAllByOrderByFechaDesc();
        model.addAttribute("logs", logs);
        return "mensajes_log";
    }
}
