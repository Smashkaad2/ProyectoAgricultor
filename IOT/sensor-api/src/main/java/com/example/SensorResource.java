package com.example;

import com.example.model.Sensor;
import com.example.repository.SensorRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @Inject
    SensorRepository sensorRepository;

    @Inject
    SensorWebSocket sensorWebSocket;

    @GET
    public List<Sensor> getAllSensors() {
        return sensorRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Sensor getSensorById(@PathParam("id") Long id) {
        return sensorRepository.findById(id);
    }

    @POST
    @Transactional
    public Response createSensor(Sensor sensor) {
        sensorRepository.persist(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Sensor updateSensor(@PathParam("id") Long id, Sensor sensor) {
        Sensor entity = sensorRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Sensor con id " + id + " no existe");
        }
        
        entity.nombre = sensor.nombre;
        entity.ubicacion = sensor.ubicacion;
        entity.humedadSuelo = sensor.humedadSuelo;
        entity.temperatura = sensor.temperatura;
        entity.calidadAire = sensor.calidadAire;
        entity.nivelFertilizacion = sensor.nivelFertilizacion;
        
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void deleteSensor(@PathParam("id") Long id) {
        sensorRepository.deleteById(id);
    }

    @GET
    @Path("/ubicacion/{ubicacion}")
    public List<Sensor> getByUbicacion(@PathParam("ubicacion") String ubicacion) {
        return sensorRepository.findByUbicacion(ubicacion);
    }

    @GET
    @Path("/nombre/{nombre}")
    public List<Sensor> getByNombre(@PathParam("nombre") String nombre) {
        return sensorRepository.findByNombre(nombre);
    }

    @PUT  // Cambiado de POST a PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)  // Asegúrate de tener esta anotación
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateSensorData(SensorUpdateRequest request) {
        try {
            Sensor sensor = sensorRepository.findById(request.id);
            if (sensor == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Sensor no encontrado").build();
            }
            
            sensor.humedadSuelo = request.humedadSuelo;
            sensor.temperatura = request.temperatura;
            sensor.calidadAire = request.calidadAire;
            sensor.nivelFertilizacion = request.nivelFertilizacion;
            
            sensorWebSocket.broadcast(sensor);
            
            return Response.ok(sensor).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error al actualizar: " + e.getMessage()).build();
        }
    }

    public static class SensorUpdateRequest {
        public Long id;
        public Double humedadSuelo;
        public Double temperatura;
        public Integer calidadAire;
        public Double nivelFertilizacion;
    }
}