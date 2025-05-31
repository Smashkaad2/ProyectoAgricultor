package com.example;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMessage;
import jakarta.enterprise.context.ApplicationScoped;
import io.smallrye.common.annotation.Blocking;
import java.util.concurrent.CompletionStage;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;

@ApplicationScoped
public class RabbitMQConsumer {
    private static final Logger LOG = Logger.getLogger(RabbitMQConsumer.class);

    @PostConstruct
    void init() {
        LOG.info("🚀 Iniciando RabbitMQConsumer...");
    }

    @Incoming("sensor-updates")
    @Blocking
    public CompletionStage<Void> consume(IncomingRabbitMQMessage<JsonObject> message) {
        try {
            JsonObject payload = message.getPayload();
            LOG.infof("📥 Mensaje recibido: %s", payload.encode());
            
            // Accediendo a los nuevos campos del mensaje
            int id = payload.getInteger("id");
            double humedadSuelo = payload.getDouble("humedadSuelo");
            double temperatura = payload.getDouble("temperatura");
            int calidadAire = payload.getInteger("calidadAire");
            double nivelFertilizacion = payload.getDouble("nivelFertilizacion");
            
            LOG.infof("📊 Datos del sensor %d:\n" +
                     "💧 Humedad Suelo: %.2f%%\n" +
                     "🌡️ Temperatura: %.2f°C\n" +
                     "💨 Calidad Aire: %d ppm\n" +
                     "🌱 Nivel Fertilización: %.2f%%", 
                     id, humedadSuelo, temperatura, calidadAire, nivelFertilizacion);
            
            return message.ack();
        } catch (Exception e) {
            LOG.errorf("❌ Error procesando mensaje: %s", e.getMessage());
            return message.nack(e);
        }
    }
}