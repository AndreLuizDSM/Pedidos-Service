package com.andre.pedidosservice.notification.gateways.out;

// Porta de saída para controle de idempotência: permite ao consumer verificar se um
// evento já foi processado antes de agir, e marcar como processado depois do sucesso
public interface NotificationDeduplicationGateway {

    boolean alreadyProcessed(String eventId);

    void markAsProcessed(String eventId);
}
