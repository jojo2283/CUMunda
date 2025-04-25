package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetTaskData implements JavaDelegate {
    private final AnswerRepository answerRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Object value = delegateExecution.getVariable("answer_to_check");
        Long answerId = value instanceof Long
                ? (Long) value
                : ((Number) value).longValue();


        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("answer not found"));
        answer.setNeedVerify(Boolean.FALSE);
        delegateExecution.setVariable("assignment_text", answer.getAssignment().getDescription());
        delegateExecution.setVariable("user_answer", answer.getAnswerText());
    }
}
