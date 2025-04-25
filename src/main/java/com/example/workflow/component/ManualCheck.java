package com.example.workflow.component;

import com.example.workflow.entities.Role;
import com.example.workflow.entities.User;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component(value = "manualCheck")
public class ManualCheck implements JavaDelegate {


    private final AuthorizationService authorizationService;

    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        //нахуй не надо просто забрать шаблон


        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();

        // Получаем пользователя из своей базы
        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Только если роль — EDUCATOR
        if (user.getRoles().contains(Role.EDUCATOR)) {


            delegateExecution.setVariable("correct", Boolean.TRUE);
            System.out.println("Модератор принял ответ: " + userId);
        } else {
            System.out.println("Пользователь " + userId + " не имеет прав EDUCATOR");

            throw new RuntimeException("Недостаточно прав");
        }
    }
}

