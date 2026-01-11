package com.org.emprunt.service;

import com.org.emprunt.event.EmpruntEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmpruntProducer {

    private static final String TOPIC = "emprunt-created";
    
    private final KafkaTemplate<String, EmpruntEvent> kafkaTemplate;

    public EmpruntProducer(KafkaTemplate<String, EmpruntEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmpruntEvent(EmpruntEvent event) {
        System.out.println("Publication evenement Kafka: " + event);
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Evenement publie sur le topic: " + TOPIC);
    }
}