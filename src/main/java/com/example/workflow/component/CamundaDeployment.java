package com.example.workflow.component;

import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamundaDeployment {

    @Autowired
    private RepositoryService repositoryService;

    @PostConstruct
    public void deployProcessAndForm() {
        repositoryService.createDeployment()
                .name("course-process")
                .addClasspathResource("blps.bpmn")
                .addClasspathResource("static/forms/selectCourse.html")
                .deploy();

        System.out.println("✅ Deployed selectCourse.bpmn and selectCourse.form");
    }
}