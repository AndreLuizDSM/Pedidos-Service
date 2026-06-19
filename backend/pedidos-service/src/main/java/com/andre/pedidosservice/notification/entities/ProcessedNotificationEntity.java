package com.andre.pedidosservice.notification.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Registro de eventos de notificação já processados com sucesso. Usado para detectar
// reentregas do RabbitMQ (at-least-once delivery) e evitar enviar o mesmo e-mail duas vezes
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_processed_notification")
public class ProcessedNotificationEntity {

    @Id
    private String eventId;

    private LocalDateTime processedAt;
}
