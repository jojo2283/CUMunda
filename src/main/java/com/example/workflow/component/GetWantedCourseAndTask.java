package com.example.workflow.component;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("selectTask")
public class GetWantedCourseAndTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("Выбор задания пользователем...");
        execution.setVariable("taskType", "open"); // Пример
    }
}
