package com.andre.pedidosservice.notification.ports.out;

import com.andre.pedidosservice.notification.entities.ProcessedNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedNotificationJpaRepository extends JpaRepository<ProcessedNotificationEntity, String> {
}
