package com.example.workflow.component;

import com.example.workflow.entities.*;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@RequiredArgsConstructor
@Component
public class SendAnswer implements JavaDelegate {

    private  final AssignmentRepository assignmentRepository;
    private  final AnswerRepository answerRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));
        String answertext = (String) delegateExecution.getVariable("answer_text");

        Answer answer = new Answer();
        answer.setAnswerText(answertext);
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(()->new NoSuchAssigmentException("Assignment not found!"));
        answer.setAssignment(assignment);


        if (assignment.isRequiresManualReview()) {
            answer.setNeedVerify(Boolean.TRUE);
            answerRepository.save(answer);
            delegateExecution.setVariable("isNeedToVerify",true);
            return;
        }
        answer.setNeedVerify(Boolean.FALSE);


        User user = new User();
        user.setRoles(new HashSet<>(List.of(Role.USER)));
        user.setEmail("no-email@example.com");
        answer.setUser(user);

         answerRepository.save(answer);
        delegateExecution.setVariable("answer_id", answer.getId());
        delegateExecution.setVariable("isNeedToVerify", Boolean.FALSE);
        System.out.println("Sending answer!");
    }
}
