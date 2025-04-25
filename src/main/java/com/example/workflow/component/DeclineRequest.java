package com.example.workflow.component;

import com.example.workflow.entities.Role;
import com.example.workflow.entities.User;
import com.example.workflow.repositories.ReturnRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeclineRequest implements JavaDelegate {
    private final ReturnRepository returnRepository;
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
            returnRepository.deleteById(requestId);


            System.out.println("Админ принял ответ: " + userId);
        } else {
            System.out.println("Пользователь " + userId + " не имеет прав ADMIN");

            throw new RuntimeException("Недостаточно прав");
        }
    }
}
