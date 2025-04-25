package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Product;
import com.example.workflow.entities.User;
import com.example.workflow.exception.NoSuchProductException;
import com.example.workflow.kafka.EmailRequest;
import com.example.workflow.kafka.KafkaProducer;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotEnoughtMoneyMessage implements JavaDelegate {
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final AnswerRepository answerRepository;
    private final ProductRepository productRepository;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Long answerId = (Long) delegateExecution.getVariable("answer_id");
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("Answer not found"));

        Long productId = Long.parseLong((String) delegateExecution.getVariable("product_id"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("Product not found"));


        int price = product.getPrice();

        User user = answer.getUser();
        try {
            String json = objectMapper.writeValueAsString(new EmailRequest(
                    user.getEmail(),
                    "Навык коробка",
                    "У вас недостаточно баллов. Наберите еще  " + Math.abs(user.getPoints() - price) + " баллов."

            ));
            kafkaProducer.sendMessage(json);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException();
        }
    }
}