package com.dcc.actividades.repository;

import com.dcc.actividades.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Actividad.
 * 
 * Seguridad: Todas las consultas usan parámetros nombrados (:query)
 * que previenen inyección SQL. Nunca se concatenan strings del usuario
 * directamente en la consulta.
 */
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    /**
     * Busca actividades cuyo nombre, tipo o nombre de comuna del miembro
     * contengan el texto de búsqueda (case-insensitive).
     * 
     * Usa LOWER() + LIKE con parámetro nombrado para prevenir SQL Injection.
     * El parámetro :query debe pasarse ya envuelto en '%' desde el servicio.
     */
    @Query("SELECT a FROM Actividad a " +
           "JOIN a.miembro m " +
           "JOIN m.comuna c " +
           "WHERE LOWER(a.nombre) LIKE LOWER(:query) " +
           "OR LOWER(a.tipo) LIKE LOWER(:query) " +
           "OR LOWER(a.enlace) LIKE LOWER(:query) " +
           "OR LOWER(c.nombre) LIKE LOWER(:query)")
    List<Actividad> buscarPorTexto(@Param("query") String query);
}
