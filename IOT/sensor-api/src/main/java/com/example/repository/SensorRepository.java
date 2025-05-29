package com.example.repository;

import com.example.model.Sensor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SensorRepository implements PanacheRepository<Sensor> {
    
    public List<Sensor> findByUbicacion(String ubicacion) {
        return list("ubicacion", ubicacion);
    }
    
    public List<Sensor> findByNombre(String nombre) {
        return list("nombre", nombre);
    }
}