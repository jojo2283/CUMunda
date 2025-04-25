package com.example.workflow.component;

import com.example.workflow.entities.Product;
import com.example.workflow.entities.Transaction;
import com.example.workflow.entities.User;
import com.example.workflow.exception.NoSuchProductException;
import com.example.workflow.repositories.ProductRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetPayment implements JavaDelegate {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();


        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Long productId = Long.parseLong((String) delegateExecution.getVariable("product_id"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new NoSuchProductException("Product not found"));


        int price = product.getPrice();
        user.setPoints(user.getPoints() - price);
        userRepository.save(user);

        Transaction transaction = new Transaction(user, product, price);
        transactionRepository.save(transaction);



    }
}
