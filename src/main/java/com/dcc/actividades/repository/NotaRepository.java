package com.dcc.actividades.repository;

import com.dcc.actividades.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Nota.
 * Provee métodos para calcular el promedio y contar las evaluaciones
 * asociadas a una actividad específica.
 */
@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {

    /**
     * Calcula el promedio de notas para una actividad dada.
     * Retorna null si no hay notas registradas.
     */
    @Query("SELECT AVG(n.nota) FROM Nota n WHERE n.actividad.id = :actividadId")
    Double promedioByActividadId(@Param("actividadId") Integer actividadId);

    /**
     * Cuenta el número de evaluaciones para una actividad dada.
     */
    @Query("SELECT COUNT(n) FROM Nota n WHERE n.actividad.id = :actividadId")
    Long contarByActividadId(@Param("actividadId") Integer actividadId);
}
