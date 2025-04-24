package com.example.workflow.component;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.stereotype.Component;

@Component(value ="giveAnswer")
public class GiveAnswer implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("Пользователь дал ответ");

        // Получение глобальной переменной
        String courseId = (String) delegateExecution.getVariable("course_id");
        System.out.println("Имя пользователя: " + courseId);

        // Установка новой переменной
//        delegateExecution.setVariable("taskType", "open");
    }


}
