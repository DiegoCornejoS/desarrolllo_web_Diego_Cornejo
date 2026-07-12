package com.dcc.actividades.repository;

import com.dcc.actividades.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad LogEntry.
 * Permite listar todos los registros de log ordenados por fecha descendente.
 */
@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Lista todos los registros de log ordenados del más reciente al más antiguo.
     */
    List<LogEntry> findAllByOrderByFechaDesc();
}
