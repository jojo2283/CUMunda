package com.example.workflow.component;

import com.example.workflow.connector.LineItemConnectionFactory;
import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.example.workflow.entities.User;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.AssignmentRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.model.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import com.example.workflow.connector.*;

@RequiredArgsConstructor
@Component
public class SendAnswer implements JavaDelegate {

    private final AssignmentRepository assignmentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    private static String calculateBodyHash(String body) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(body.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));
        String answertext = (String) delegateExecution.getVariable("answer_text");

        Answer answer = new Answer();
        answer.setAnswerText(answertext);
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new NoSuchAssigmentException("Assignment not found!"));
        answer.setAssignment(assignment);

        delegateExecution.setVariable("isNeedToVerify", assignment.isRequiresManualReview());
        answer.setNeedVerify(assignment.isRequiresManualReview());


        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();


        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));


        answer.setUser(user);
        LineItemConnectionFactory factory = new LineItemConnectionFactory(
                "jisc.ac.uk",
                "secret",
                "https://saltire.lti.app/platform/gradebook/s7a7425cc04a3236d856e4d6badf23f5c/S3294476/lineitems"
        );

        Answer newAnswer = answerRepository.save(answer);

        try (LineItemConnection connection = factory.getConnection()) {
            Response response = connection.createLineItem(assignment, newAnswer);
            System.out.println("Response status: " + response.getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userRepository.save(answer.getUser());

        delegateExecution.setVariable("answer_id", answer.getId());
        System.out.println("Sending answer!");
    }
}
