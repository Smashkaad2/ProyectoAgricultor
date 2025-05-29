package com.example.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Sensor extends PanacheEntity {
    
    public String nombre;
    public String ubicacion;
    
    // Datos del sensor
    public Double humedadSuelo;  // en porcentaje (0-100)
    public Double temperatura;   // en grados Celsius
    public Integer calidadAire;  // índice de calidad (1-500)
    public Double nivelFertilizacion; // en ppm (partes por millón)
    
    public LocalDateTime fechaRegistro = LocalDateTime.now();

    // Constructor vacío necesario para JPA
    public Sensor() {
    }

    // Constructor con parámetros
    public Sensor(String nombre, String ubicacion, Double humedadSuelo, 
                 Double temperatura, Integer calidadAire, Double nivelFertilizacion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.humedadSuelo = humedadSuelo;
        this.temperatura = temperatura;
        this.calidadAire = calidadAire;
        this.nivelFertilizacion = nivelFertilizacion;
    }
}