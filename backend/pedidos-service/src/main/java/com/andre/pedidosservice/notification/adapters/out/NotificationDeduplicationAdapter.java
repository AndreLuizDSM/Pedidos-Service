package com.andre.pedidosservice.notification.adapters.out;

import com.andre.pedidosservice.notification.entities.ProcessedNotificationEntity;
import com.andre.pedidosservice.notification.gateways.out.NotificationDeduplicationGateway;
import com.andre.pedidosservice.notification.ports.out.ProcessedNotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationDeduplicationAdapter implements NotificationDeduplicationGateway {

    private final ProcessedNotificationJpaRepository repository;

    @Override
    public boolean alreadyProcessed(String eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(String eventId) {
        repository.save(new ProcessedNotificationEntity(eventId, LocalDateTime.now()));
    }
}
