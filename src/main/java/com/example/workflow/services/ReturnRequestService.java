package com.example.workflow.services;


import com.example.workflow.entities.ReturnRequestEntity;
import com.example.workflow.entities.Transaction;
import com.example.workflow.entities.request.TransactionToPurchase;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.TransactionRepository;
import com.example.workflow.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ReturnRequestService {
    private  final TransactionTemplate transactionTemplate;
    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private  KafkaProducer kafkaProducer;
    @Autowired
    private  ObjectMapper objectMapper;

    public ReturnRequestService(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setTimeout(30);
    }



    public ReturnRequestEntity createReturnRequest(TransactionToPurchase transactionToPurchase) {
        return transactionTemplate.execute(status -> {

            Transaction transaction = transactionRepository.findById(transactionToPurchase.getTransactionId()).orElseThrow(() -> new RuntimeException("Transaction not found"));
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

//            try {
//                List<User> allUsers = userRepository.findAll();
//                List<User> admins = allUsers.stream()
//                        .filter(user -> user.getRoles().contains(Role.ADMIN))
//                        .toList();
//                for (User admin : admins) {
//                    String json = objectMapper.writeValueAsString(new EmailRequest(
//                            admin.getEmail(),
//                            "Навык коробка",
//                            "Появилась новая заявка на возврат товара "+ transaction.getProduct().getName()+". Пользователь "+transaction.getUser().getUsername() +" email: "+ transaction.getUser().getEmail()
//                    ));
//                    kafkaProducer.sendMessage(json);
//                }
//
//            } catch (JsonProcessingException ex) {
//                throw new RuntimeException();
//            }

            return returnRequest;
        });
    }




}