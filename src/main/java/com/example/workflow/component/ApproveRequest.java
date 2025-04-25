package com.example.workflow.component;

import com.example.workflow.entities.ReturnRequestEntity;
import com.example.workflow.entities.Transaction;
import com.example.workflow.entities.User;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApproveRequest implements JavaDelegate {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ReturnRepository returnRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Object value = delegateExecution.getVariable("request_id");
        Long requestId = value instanceof Long
                ? (Long) value
                : ((Number) value).longValue();


        ReturnRequestEntity request = returnRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Return request not found"));
        Transaction transaction = transactionRepository.findById(request.getTransactionId()).orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setRefunded(true);

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPoints(user.getPoints() + request.getPointsUsed());
        userRepository.save(user);
        transactionRepository.save(transaction);
        returnRepository.deleteById(requestId);
    }
}
