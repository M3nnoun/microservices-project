package com.library.notification.service;

import com.library.notification.entity.Notification;
import com.library.notification.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    
    private final NotificationRepository notificationRepository;
    
    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @KafkaListener(topics = "emprunt-created", groupId = "notification-group")
    public void handleEmpruntCreated(String message) {
        System.out.println("Recu evenement emprunt-created: " + message);
        
        Notification notification = new Notification();
        notification.setType("EMPRUNT_CREATED");
        notification.setMessage("Nouvel emprunt cree: " + message);
        
        notificationRepository.save(notification);
        System.out.println("Notification sauvegardee");
    }
}