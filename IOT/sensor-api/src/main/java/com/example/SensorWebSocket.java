package com.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/sensor-data")
@ApplicationScoped
public class SensorWebSocket {
    private static final Logger LOG = Logger.getLogger(SensorWebSocket.class);
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Jsonb jsonb = JsonbBuilder.create();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        LOG.infof("Nueva conexión WebSocket: %s", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        LOG.infof("Conexión cerrada: %s", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        LOG.errorf("Error en WebSocket: %s", throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message) {
        LOG.infof("Mensaje recibido: %s", message);
    }

    public void broadcast(Object message) {
        String jsonMessage = jsonb.toJson(message);
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendText(jsonMessage, result -> {
                if (result.getException() != null) {
                    LOG.error("Error al enviar mensaje", result.getException());
                }
            });
        });
    }
}