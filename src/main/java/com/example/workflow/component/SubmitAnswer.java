package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.AnswerDTO;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.repositories.AssignmentRepository;
import com.example.workflow.services.AnswerService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.awt.*;


@RequiredArgsConstructor
@Component
public class SubmitAnswer  implements JavaDelegate {

    private  final AnswerService answerService;
    private  final AssignmentRepository assignmentRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String courseId = (String) delegateExecution.getVariable("course_id");
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));

        String answertext = (String) delegateExecution.getVariable("answer_text");
        Answer answer = new Answer();
        answer.setAnswerText(answertext);
        answer.setAssignment(assignmentRepository.findById(assignmentId).orElseThrow(()->new NoSuchAssigmentException("Assignment not found!")));
        AnswerDTO answerDTO = answerService.submit(answer);
        delegateExecution.setVariable("isNeedToVerify", answerDTO.getNeedVerify());
        delegateExecution.setVariable("correct", answerDTO.getIsCorrect());
    }
}
