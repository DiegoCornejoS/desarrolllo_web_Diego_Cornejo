package com.dcc.actividades.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para la solicitud de evaluación de actividad.
 * 
 * Seguridad: Usa Bean Validation (Jakarta Validation) para garantizar que
 * el valor de la nota sea un entero entre 1 y 7 inclusive. Spring Boot
 * rechazará automáticamente con HTTP 400 cualquier valor fuera de rango
 * o nulo cuando se usa con @Valid en el controlador.
 */
public class EvaluarRequest {

    @NotNull(message = "La nota es obligatoria.")
    @Min(value = 1, message = "La nota mínima es 1.")
    @Max(value = 7, message = "La nota máxima es 7.")
    private Integer valor;

    public EvaluarRequest() {
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }
}
