package com.devsenior.jmorera.bibliokeep.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.jmorera.bibliokeep.model.dto.notification.Notification;

@RestController
@Controller
public class NotificationController {

    // 1. Recibe mensajes en /app/nueva-cita (desde el cliente o servicio interno)
    // 2. Reenvía automáticamente lo que retorne a /topic/appointments
    @MessageMapping("/nueva-cita")
    @SendTo("/topic/appointments")
    public Notification notificarCita(Notification notificacion) {
        // Aquí podrías guardar logs o procesar datos
        System.out.println("Notificando nueva cita de: " + notificacion.paciente);
        return notificacion;
    }

    // NOTA AVANZADA: Para enviar desde un @Service normal (ej. cuando se guarda en
    // DB),
    // debes inyectar 'SimpMessagingTemplate' y usar:
    // template.convertAndSend("/topic/appointments", objeto);
}
