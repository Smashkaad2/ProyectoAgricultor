package com.example;

import com.example.model.Sensor;
import com.example.repository.SensorRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMessage;
import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.common.annotation.Blocking;
import java.util.concurrent.CompletionStage;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class RabbitMQConsumer {
    private static final Logger LOG = Logger.getLogger(RabbitMQConsumer.class);

    @Inject
    SensorRepository sensorRepository;

    @Inject
    SensorWebSocket sensorWebSocket;

    @Incoming("sensor-updates")
    @Blocking
    @Transactional
    public CompletionStage<Void> consume(IncomingRabbitMQMessage<JsonObject> message) {
        try {
            JsonObject payload = message.getPayload();
            LOG.infof("📥 Mensaje recibido: %s", payload.encode());
            
            // Extraer datos del mensaje
            Long id = payload.getLong("id");
            double humedadSuelo = payload.getDouble("humedadSuelo");
            double temperatura = payload.getDouble("temperatura");
            int calidadAire = payload.getInteger("calidadAire");
            double nivelFertilizacion = payload.getDouble("nivelFertilizacion");
            
            // Buscar el sensor en la base de datos
            Sensor sensor = sensorRepository.findById(id);
            if (sensor == null) {
                LOG.errorf("❌ Sensor con ID %d no encontrado", id);
                return message.nack(new NotFoundException("Sensor no encontrado"));
            }
            
            // Actualizar valores
            sensor.setHumedadSuelo(humedadSuelo);
            sensor.setTemperatura(temperatura);
            sensor.setCalidadAire(calidadAire);
            sensor.setNivelFertilizacion(nivelFertilizacion);
            
            // Persistir cambios (no necesario si el método es @Transactional)
            sensorRepository.persist(sensor);
            
            LOG.infof("✅ Sensor %d actualizado:\n" +
                     "💧 Humedad Suelo: %.2f%%\n" +
                     "🌡️ Temperatura: %.2f°C\n" +
                     "💨 Calidad Aire: %d ppm\n" +
                     "🌱 Nivel Fertilización: %.2f%%", 
                     id, humedadSuelo, temperatura, calidadAire, nivelFertilizacion);
            
            // Notificar a los clientes WebSocket
            sensorWebSocket.broadcast(sensor);
            
            return message.ack();
        } catch (Exception e) {
            LOG.errorf("❌ Error procesando mensaje: %s", e.getMessage());
            return message.nack(e);
        }
    }
}