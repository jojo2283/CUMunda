package com.example.workflow.component;

import com.example.workflow.repositories.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeclineRequest implements JavaDelegate {
    private final ReturnRepository returnRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Object value = delegateExecution.getVariable("request_id");
        Long requestId = value instanceof Long
                ? (Long) value
                : ((Number) value).longValue();
        returnRepository.deleteById(requestId);
    }
}
