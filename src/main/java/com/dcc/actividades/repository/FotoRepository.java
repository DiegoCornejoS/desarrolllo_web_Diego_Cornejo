package com.dcc.actividades.repository;

import com.dcc.actividades.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Foto.
 * Provee consultas para listar fotos vigentes/eliminadas y conteos para estadísticas.
 */
@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {

    /**
     * Lista todas las fotos no eliminadas, ordenadas por fecha de registro
     * del miembro asociado (más reciente primero).
     */
    @Query("SELECT f FROM Foto f " +
           "JOIN f.actividad a " +
           "JOIN a.miembro m " +
           "WHERE f.eliminada = false " +
           "ORDER BY m.fechaRegistro DESC")
    List<Foto> findFotosVigentesOrdenadas();

    /**
     * Cuenta el total de fotos vigentes (no eliminadas).
     */
    long countByEliminadaFalse();

    /**
     * Cuenta el total de fotos eliminadas (soft-delete).
     */
    long countByEliminadaTrue();
}
