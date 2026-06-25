package com.dcc.actividades.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que mapea la tabla "actividad" existente en la base de datos.
 * Cada actividad pertenece a un miembro. La relación con el miembro permite
 * acceder a la comuna del miembro para el buscador.
 */
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "miembro_id", nullable = false, insertable = false, updatable = false)
    private Integer miembroId;

    @Column(name = "dia", nullable = false, length = 20)
    private String dia;

    @Column(name = "hora_inicio", nullable = false, length = 5)
    private String horaInicio;

    @Column(name = "hora_fin", nullable = false, length = 5)
    private String horaFin;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "enlace", length = 500)
    private String enlace;

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private Miembro miembro;

    public Actividad() {
    }

    // --- Getters y Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Integer miembroId) {
        this.miembroId = miembroId;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }
}
