package com.example.workflow.component;

import com.example.workflow.entities.ReturnRequestEntity;
import com.example.workflow.entities.Transaction;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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



    }
}
