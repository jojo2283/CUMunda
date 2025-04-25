package com.example.workflow.component;

import com.example.workflow.entities.*;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetRequestData implements JavaDelegate {
    private final ReturnRepository returnRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();


        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getRoles().contains(Role.ADMIN)) {
            Object value = delegateExecution.getVariable("request_id");
            Long requestId = value instanceof Long
                    ? (Long) value
                    : ((Number) value).longValue();

            ReturnRequestEntity request = returnRepository.findById(requestId).orElse(null);
            Transaction transaction = transactionRepository.findById(request.getTransactionId()).orElse(null);

            delegateExecution.setVariable("transacton_username", transaction.getUser().getUsername());
            delegateExecution.setVariable("transaction_productname", transaction.getProduct().getName());
            delegateExecution.setVariable("transaction_points",transaction.getProduct().getPrice());




            System.out.println("Админ принял ответ: " + userId);
        } else {
            System.out.println("Пользователь " + userId + " не имеет прав ADMIN");

            throw new RuntimeException("Недостаточно прав");
        }
    }
}
