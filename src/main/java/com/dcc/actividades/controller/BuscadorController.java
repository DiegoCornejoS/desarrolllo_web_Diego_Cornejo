package com.dcc.actividades.controller;

import com.dcc.actividades.model.Actividad;
import com.dcc.actividades.model.Nota;
import com.dcc.actividades.repository.ActividadRepository;
import com.dcc.actividades.repository.NotaRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador principal de la Tarea 4.
 * 
 * Maneja:
 *  - GET /buscador           → Sirve la plantilla Thymeleaf del buscador
 *  - GET /api/actividades/buscar?q=... → API REST de búsqueda de actividades
 *  - POST /api/actividades/{id}/evaluar → API REST para agregar una nota
 * 
 * Seguridad implementada:
 *  - Búsqueda: parámetros JPQL nombrados (previene SQL injection)
 *  - Evaluación: validación con @Valid + Bean Validation (1 ≤ nota ≤ 7)
 *  - Errores: nunca se exponen detalles internos al cliente
 *  - XSS: el frontend (buscador.html) escapa todo con escapeHTML() antes de inyectar
 */
@Controller
public class BuscadorController {

    private final ActividadRepository actividadRepository;
    private final NotaRepository notaRepository;

    public BuscadorController(ActividadRepository actividadRepository,
                              NotaRepository notaRepository) {
        this.actividadRepository = actividadRepository;
        this.notaRepository = notaRepository;
    }

    /**
     * Sirve la página del buscador de actividades (Thymeleaf).
     */
    @GetMapping("/buscador")
    public String buscador() {
        return "buscador";
    }

    /**
     * API REST: Busca actividades por nombre, tipo o comuna.
     * Requiere al menos 3 caracteres en el parámetro "q".
     * 
     * Retorna una lista JSON con los datos de cada actividad, incluyendo
     * la nota promedio y la cantidad de evaluaciones.
     * 
     * Seguridad: el parámetro de búsqueda se usa exclusivamente como
     * parámetro nombrado JPQL (nunca se concatena en la consulta SQL).
     */
    @GetMapping("/api/actividades/buscar")
    @ResponseBody
    public ResponseEntity<?> buscar(@RequestParam(name = "q", defaultValue = "") String query) {
        // Validar largo mínimo de búsqueda
        String queryTrimmed = query.trim();
        if (queryTrimmed.length() < 3) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La búsqueda requiere al menos 3 caracteres."));
        }

        // Limitar largo máximo de búsqueda para prevenir abusos
        if (queryTrimmed.length() > 200) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La búsqueda no puede exceder 200 caracteres."));
        }

        try {
            // Envolver en % para LIKE, usando parámetro JPQL nombrado (seguro contra SQL injection)
            String patron = "%" + queryTrimmed + "%";
            List<Actividad> resultados = actividadRepository.buscarPorTexto(patron);

            List<Map<String, Object>> respuesta = new ArrayList<>();
            for (Actividad act : resultados) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", act.getId());
                item.put("nombreActividad", act.getNombre());
                item.put("nombreMiembro", act.getMiembro() != null ? act.getMiembro().getNombre() : "");
                item.put("dia", act.getDia());
                item.put("tipo", act.getTipo());
                item.put("comuna", act.getMiembro() != null && act.getMiembro().getComuna() != null
                        ? act.getMiembro().getComuna().getNombre() : "");
                item.put("descripcion", act.getEnlace() != null ? act.getEnlace() : "");

                // Obtener nota promedio y cantidad de evaluaciones
                Double promedio = notaRepository.promedioByActividadId(act.getId());
                Long cantidad = notaRepository.contarByActividadId(act.getId());

                if (promedio != null) {
                    // Redondear a 1 decimal
                    BigDecimal bd = BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP);
                    item.put("nota", bd.toString());
                } else {
                    item.put("nota", "-");
                }
                item.put("cantNotas", cantidad != null ? cantidad : 0);

                respuesta.add(item);
            }

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            // Seguridad: no exponer detalles internos del error al cliente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la búsqueda."));
        }
    }

    /**
     * API REST: Agrega una nueva evaluación (nota) a una actividad.
     * 
     * Seguridad:
     *  - Validación con @Valid: nota debe ser entero entre 1 y 7
     *  - Verifica que la actividad exista antes de insertar
     *  - No expone errores internos al cliente
     */
    @PostMapping("/api/actividades/{id}/evaluar")
    @ResponseBody
    public ResponseEntity<?> evaluar(@PathVariable("id") Integer actividadId,
                                     @Valid @RequestBody EvaluarRequest request,
                                     BindingResult bindingResult) {

        // Validación de Bean Validation (nota null, fuera de rango, etc.)
        if (bindingResult.hasErrors()) {
            List<String> errores = new ArrayList<>();
            bindingResult.getAllErrors().forEach(error -> errores.add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", errores));
        }

        try {
            // Verificar que la actividad existe
            Optional<Actividad> actividadOpt = actividadRepository.findById(actividadId);
            if (actividadOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Actividad no encontrada."));
            }

            // Crear y guardar la nueva nota
            Nota nuevaNota = new Nota();
            nuevaNota.setActividad(actividadOpt.get());
            nuevaNota.setNota(request.getValor());
            notaRepository.save(nuevaNota);

            // Recalcular el promedio y la cantidad de notas
            Double nuevoPromedio = notaRepository.promedioByActividadId(actividadId);
            Long nuevaCantidad = notaRepository.contarByActividadId(actividadId);

            Map<String, Object> respuesta = new HashMap<>();
            if (nuevoPromedio != null) {
                BigDecimal bd = BigDecimal.valueOf(nuevoPromedio).setScale(1, RoundingMode.HALF_UP);
                respuesta.put("notaPromedio", bd.toString());
            } else {
                respuesta.put("notaPromedio", "-");
            }
            respuesta.put("cantNotas", nuevaCantidad != null ? nuevaCantidad : 0);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            // Seguridad: no exponer detalles internos del error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al guardar la evaluación."));
        }
    }
}
