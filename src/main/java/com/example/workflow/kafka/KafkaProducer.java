package com.example.workflow.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String,String > kafkaTemplate;


    public void sendMessage(String  message){
        kafkaTemplate.send("email_requests", message);
    }
}
