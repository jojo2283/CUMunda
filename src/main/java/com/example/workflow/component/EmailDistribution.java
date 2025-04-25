package com.example.workflow.component;

import com.example.workflow.entities.User;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component(value ="emailDistribution")
public class EmailDistribution implements JavaDelegate {

    private final  UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        List<User> users = userRepository.findByPointsGreaterThan(0);
        for (User user : users) {
            System.out.println("Шлем имейл на: " + user.getEmail());
        }
    }
}

