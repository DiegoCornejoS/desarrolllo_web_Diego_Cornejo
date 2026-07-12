package com.dcc.actividades.controller;

import com.dcc.actividades.repository.FotoRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Controlador para la funcionalidad 3 de la Tarea 5: Estadística de Fotos.
 * 
 * Acceso público (sin autenticación).
 * Muestra un gráfico con el total de fotos vigentes vs eliminadas.
 */
@Controller
public class EstadisticaFotosController {

    private final FotoRepository fotoRepository;

    public EstadisticaFotosController(FotoRepository fotoRepository) {
        this.fotoRepository = fotoRepository;
    }

    /**
     * Sirve la página pública con el gráfico de estadísticas de fotos.
     */
    @GetMapping("/estadistica-fotos")
    public String estadisticaFotos() {
        return "estadistica_fotos";
    }

    /**
     * API REST pública que retorna los conteos de fotos vigentes y eliminadas
     * en formato JSON para alimentar el gráfico Chart.js.
     */
    @GetMapping("/api/estadistica-fotos")
    @ResponseBody
    public ResponseEntity<?> apiEstadisticaFotos() {
        try {
            long vigentes = fotoRepository.countByEliminadaFalse();
            long eliminadas = fotoRepository.countByEliminadaTrue();
            return ResponseEntity.ok(Map.of("vigentes", vigentes, "eliminadas", eliminadas));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener estadísticas de fotos."));
        }
    }
}
