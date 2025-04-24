package com.example.workflow.services;


import com.example.workflow.entities.*;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.AssignmentRepository;
import com.example.workflow.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class AnswerService {
    private final TransactionTemplate transactionTemplate;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    //    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public AnswerService(PlatformTransactionManager transactionManager, AnswerRepository answerRepository,
                         UserRepository userRepository, AssignmentRepository assignmentRepository,
            /* KafkaProducer kafkaProducer,*/ ObjectMapper objectMapper) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setTimeout(30);
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
//        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    private static String calculateBodyHash(String body) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(body.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }


    public AnswerDTO submit(Answer answer) throws NoSuchAlgorithmException {
        return transactionTemplate.execute(status -> {
            Assignment assignment = assignmentRepository.findById(answer.getAssignment().getId())
                    .orElseThrow(() -> new NoSuchAssigmentException("no such assignment"));
//            Optional<User> userOpt = userRepository.findById(answer.getUser().getId());

            if (assignment.isRequiresManualReview()) {
                answer.setNeedVerify(Boolean.TRUE);
                Answer newAnswer = answerRepository.save(answer);
//                User student = userOpt.get();
                List<User> allUsers = userRepository.findAll();
//                List<User> educators = allUsers.stream()
//                        .filter(user -> user.getRoles().contains(Role.EDUCATOR))
//                        .toList();
//                try {
//                    for (User user : educators) {
//                        String json = objectMapper.writeValueAsString(new EmailRequest(
//                                user.getEmail(),
//                                "Навык коробка",
//                                "Появилось новое задание для проверки. Пользователь " + student.getUsername() + " email: " + student.getEmail() + " выполнил задание."
//
//                        ));
//                        kafkaProducer.sendMessage(json);
//                    }
//                } catch (JsonProcessingException ex) {
//                    throw new RuntimeException();
//                }

//                LineItemConnectionFactory factory = new LineItemConnectionFactory(
//                        "secret",
//                        "secret",
//                        "https://saltire.lti.app/platform/gradebook/c1fba6c388a84a64bd68cfddd8fdfa89/S3294476/lineitems"
//                );

//                try (LineItemConnection connection = factory.getConnection()) {
//                    Response response = connection.createLineItem(assignment, newAnswer);
//                    System.out.println("Response status: " + response.getCode());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                return new AnswerDTO(newAnswer.getId(), answer.getAnswerText(), newAnswer.getIsCorrect(),
                        "Задание отправлено на проверку", assignment.getId(),Boolean.TRUE);
            }

//            if (userOpt.isEmpty()) {
//                return new AnswerDTO(null, answer.getAnswerText(), null, "User not found", assignment.getId());
//            }

//            User user = userOpt.get();
            User user = new User();
            user.setRoles(new HashSet<>(Arrays.asList(Role.USER)));
            user.setEmail("no-email@example.com");
//            boolean alreadyCorrect = answerRepository.existsByUserAndAssignmentAndIsCorrectTrue(user, assignment);
//            if (alreadyCorrect) {
//                return new AnswerDTO(null, answer.getAnswerText(), null,
//                        "You have already correctly answered this assignment.", assignment.getId());
//            }

            boolean isCorrect = answer.getAnswerText().trim().equalsIgnoreCase(assignment.getCorrectAnswer().trim());


            Answer newAnswer = new Answer();
            newAnswer.setUser(user);
            newAnswer.setAssignment(assignment);
            newAnswer.setAnswerText(answer.getAnswerText());
            newAnswer.setIsCorrect(isCorrect);
            newAnswer.setNeedVerify(Boolean.FALSE);

            if (isCorrect) {
                user.setPoints(user.getPoints() + assignment.getPoints());
                userRepository.save(user);
            }
//                try {
//                    String json = objectMapper.writeValueAsString(new EmailRequest(
//                            user.getEmail(),
//                            "Навык коробка",
//                            "Вы получили " + newAnswer.getAssignment().getPoints() + " баллов за верно выполненное задание."
//
//                    ));
//                    kafkaProducer.sendMessage(json);
//                } catch (JsonProcessingException ex) {
//                    throw new RuntimeException();
//                }

//            } else {
//                try {
//                    String json = objectMapper.writeValueAsString(new EmailRequest(
//                            user.getEmail(),
//                            "Навык коробка",
//                            "Вы неправильно ответили на задание. Попробуйте ещё раз чтобы получить " + newAnswer.getAssignment().getPoints() + " баллов за верно выполненное задание."
//
//                    ));
//                    kafkaProducer.sendMessage(json);
//                } catch (JsonProcessingException ex) {
//                    throw new RuntimeException();
//                }
//            }

                newAnswer = answerRepository.save(newAnswer);

                String message = isCorrect ? "Correct answer! Points awarded." : "Incorrect answer. Try again.";

                return new AnswerDTO(newAnswer.getId(), newAnswer.getAnswerText(), newAnswer.getIsCorrect(), message, assignment.getId(),Boolean.FALSE);


        });


    }


}

