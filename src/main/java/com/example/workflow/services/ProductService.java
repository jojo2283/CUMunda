package com.example.workflow.services;



import com.example.workflow.entities.Product;
import com.example.workflow.exception.NoSuchProductException;
import com.example.workflow.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final TransactionTemplate transactionTemplate;

    public ProductService(PlatformTransactionManager transactionManager, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setTimeout(30);
    }


    public List<Product> getProducts() {
        return transactionTemplate.execute(status -> productRepository.findAll());
    }


    public Product getProductById(Long id) {
        return transactionTemplate.execute(status -> productRepository.findById(id).orElseThrow(() -> new NoSuchProductException("Product not found")));

    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
//
//    @Transactional
//    public Product updateProduct(Long id, Product productDetails) {
//        Product product = productRepository.findById(id).orElseThrow(() -> new NoSuchProductException("Product not found"));
//
//        product.setName(productDetails.getName());
//        product.setDescription(productDetails.getDescription());
//        product.setPrice(productDetails.getPrice());
//
//        return productRepository.save(product);
//    }
//
//    @Transactional
//    public void deleteProduct(Long id) {
//        productRepository.deleteById(id);
//    }
}
