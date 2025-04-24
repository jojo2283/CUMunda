package com.example.workflow.component;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("giveAnswer")
public class GiveAnswer implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("ПОльзователь дал ответ");
        delegateExecution.setVariable("taskType", "open");
    }
}
