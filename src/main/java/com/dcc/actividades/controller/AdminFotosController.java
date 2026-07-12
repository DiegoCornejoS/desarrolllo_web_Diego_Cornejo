package com.dcc.actividades.controller;

import com.dcc.actividades.model.Foto;
import com.dcc.actividades.model.LogEntry;
import com.dcc.actividades.repository.FotoRepository;
import com.dcc.actividades.repository.LogRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para la funcionalidad 1 de la Tarea 5: Administrador de Fotos.
 * 
 * Acceso restringido al usuario cc5002/examen (rol ADMIN) mediante Spring Security.
 * 
 * Funcionalidades:
 *  - GET /admin-fotos: Galería de fotos vigentes con datos del miembro
 *  - POST /admin-fotos/eliminar/{id}: Soft-delete de una foto con registro en tabla log
 */
@Controller
public class AdminFotosController {

    private final FotoRepository fotoRepository;
    private final LogRepository logRepository;

    public AdminFotosController(FotoRepository fotoRepository, LogRepository logRepository) {
        this.fotoRepository = fotoRepository;
        this.logRepository = logRepository;
    }

    /**
     * Muestra la galería de fotos vigentes (no eliminadas),
     * ordenadas por fecha de registro del miembro (más reciente primero).
     * Cada foto muestra: imagen, fecha_registro, comuna, email del miembro.
     */
    @GetMapping("/admin-fotos")
    public String adminFotos(Model model) {
        List<Foto> fotos = fotoRepository.findFotosVigentesOrdenadas();
        model.addAttribute("fotos", fotos);
        return "admin_fotos";
    }

    /**
     * Soft-delete de una foto: marca eliminada=true y registra en tabla log.
     * 
     * Recibe JSON con campo "motivo" (obligatorio, 5-200 caracteres).
     * Inserta en log: "eliminado foto {id} por usuario admin, motivo: {motivo}"
     * 
     * Responde con JSON indicando éxito o error.
     */
    @PostMapping("/admin-fotos/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarFoto(@PathVariable("id") Integer id,
                                          @RequestBody Map<String, String> body) {
        // Validar motivo
        String motivo = body.get("motivo");
        if (motivo == null) {
            motivo = "";
        }
        motivo = motivo.trim();

        if (motivo.length() < 5 || motivo.length() > 200) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El motivo debe tener entre 5 y 200 caracteres."));
        }

        // Buscar la foto
        Optional<Foto> fotoOpt = fotoRepository.findById(id);
        if (fotoOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Foto no encontrada."));
        }

        Foto foto = fotoOpt.get();
        if (foto.getEliminada()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La foto ya fue eliminada previamente."));
        }

        try {
            // Soft-delete: marcar como eliminada
            foto.setEliminada(true);
            fotoRepository.save(foto);

            // Registrar en tabla log
            String mensaje = "eliminado foto " + id + " por usuario admin, motivo: " + motivo;
            LogEntry logEntry = new LogEntry(mensaje);
            logRepository.save(logEntry);

            return ResponseEntity.ok(Map.of("mensaje", "Foto eliminada correctamente."));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno al eliminar la foto."));
        }
    }
}
