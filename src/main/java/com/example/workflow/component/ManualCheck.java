package com.example.workflow.component;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component(value ="manualCheck")
public class ManualCheck implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        delegateExecution.setVariable("correct", Boolean.TRUE);
        System.out.println("Модератор принял ответ");
    }

}
