package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Role;
import com.example.workflow.entities.User;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetTaskData implements JavaDelegate {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {



        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();


        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (user.getRoles().contains(Role.EDUCATOR)) {

            Object value = delegateExecution.getVariable("answer_to_check");
            Long answerId = value instanceof Long
                    ? (Long) value
                    : ((Number) value).longValue();


            Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("answer not found"));
            answer.setNeedVerify(Boolean.FALSE);
            delegateExecution.setVariable("assignment_text", answer.getAssignment().getDescription());
            delegateExecution.setVariable("user_answer", answer.getAnswerText());

            System.out.println("Модератор принял ответ: " + userId);
        } else {
            System.out.println("Пользователь " + userId + " не имеет прав EDUCATOR");

            throw new RuntimeException("Недостаточно прав");
        }
    }
}
