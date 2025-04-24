package com.example.workflow.services;



import com.example.workflow.entities.Product;
import com.example.workflow.entities.Transaction;
import com.example.workflow.entities.User;
import com.example.workflow.entities.request.PurchaseRequest;
import com.example.workflow.exception.NoSuchProductException;
import com.example.workflow.exception.NoSuchUserException;
import com.example.workflow.repositories.ProductRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service

public class TransactionService {

    private final TransactionRepository transactionRepository;


    private final UserRepository userRepository;


    private final ProductRepository productRepository;

    private final TransactionTemplate transactionTemplate;
//    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public TransactionService(PlatformTransactionManager transactionManager, TransactionRepository transactionRepository,
                              UserRepository userRepository, ProductRepository productRepository,
                             /* KafkaProducer kafkaProducer,*/ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        this.transactionTemplate.setTimeout(30);
//        this.kafkaProducer = kafkaProducer;
        this.objectMapper =objectMapper;

    }


    public String purchaseProduct(PurchaseRequest request) {
        return transactionTemplate.execute(status -> {


            User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new NoSuchUserException("User not found"));
            Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NoSuchProductException("Product not found"));


            int price = product.getPrice();


            if (user.getPoints() < price) {
//                try {
//                    String json = objectMapper.writeValueAsString(new EmailRequest(
//                            user.getEmail(),
//                            "Навык коробка",
//                            "У вас недостаточно баллов. Наберите еще  " + Math.abs(user.getPoints() - price) + " баллов."
//
//                    ));
//                    kafkaProducer.sendMessage(json);
//                } catch (JsonProcessingException ex) {
//                    throw new RuntimeException();
//                }
                return "Not enough points to purchase this product";
            }


            user.setPoints(user.getPoints() - price);
            userRepository.save(user);

            Transaction transaction = new Transaction(user, product, price);
            transactionRepository.save(transaction);

//            try {
//                String json = objectMapper.writeValueAsString(new EmailRequest(
//                        user.getEmail(),
//                        "Навык коробка",
//                        "Поздравляем! Вы купили товар " + product.getName() + "."
//                ));
//                kafkaProducer.sendMessage(json);
//            } catch (JsonProcessingException ex) {
//                throw new RuntimeException();
//            }

            return "Purchase successful! Product: " + product.getName();
        });
    }





}
