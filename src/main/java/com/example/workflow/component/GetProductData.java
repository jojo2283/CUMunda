package com.example.workflow.component;

import com.example.workflow.entities.Product;
import com.example.workflow.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetProductData implements JavaDelegate {

    private final ProductRepository productRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
//        Long productId = (Long) delegateExecution.getVariable("product_id");
        Long productId = Long.parseLong((String) delegateExecution.getVariable("product_id"));

        Product product = productRepository.findById(productId).orElse(null);
        delegateExecution.setVariable("product_name", product.getName());
        delegateExecution.setVariable("product_description", product.getDescription());
        delegateExecution.setVariable("need", product.getPrice());
    }
}
