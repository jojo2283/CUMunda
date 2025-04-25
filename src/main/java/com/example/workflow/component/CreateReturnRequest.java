package com.example.workflow.component;

import com.example.workflow.entities.ReturnRequestEntity;
import com.example.workflow.entities.Role;
import com.example.workflow.entities.Transaction;
import com.example.workflow.entities.User;
import com.example.workflow.kafka.EmailRequest;
import com.example.workflow.kafka.KafkaProducer;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CreateReturnRequest implements JavaDelegate {
    private final TransactionRepository transactionRepository;
    private  final ReturnRepository returnRepository;
    private final ObjectMapper objectMapper;
    private  final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Object value = delegateExecution.getVariable("transaction_id");
        Long transactionId = value instanceof Long
                ? (Long) value
                : ((Number) value).longValue();


        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (transaction.isRefunded()) {
            throw new RuntimeException("Transaction is already refunded");
        }
        ReturnRequestEntity returnRequest = new ReturnRequestEntity();
        returnRequest.setProductId(transaction.getProduct().getId());
        returnRequest.setTransactionId(transaction.getId());
        returnRequest.setUserId(transaction.getUser().getId());
        returnRequest.setPointsUsed(transaction.getPointsUsed());
        returnRequest.setTransactionDate(transaction.getTransactionDate());

        returnRepository.save(returnRequest);

        try {
            List<User> allUsers = userRepository.findAll();
            List<User> admins = allUsers.stream()
                    .filter(user -> user.getRoles().contains(Role.ADMIN))
                    .toList();
            for (User admin : admins) {
                String json = objectMapper.writeValueAsString(new EmailRequest(
                        admin.getEmail(),
                        "Навык коробка",
                        "Появилась новая заявка на возврат товара "+ transaction.getProduct().getName()+". Пользователь "+transaction.getUser().getUsername() +" email: "+ transaction.getUser().getEmail()
                ));
                kafkaProducer.sendMessage(json);
            }

        } catch (JsonProcessingException ex) {
            throw new RuntimeException();
        }



    }
}
