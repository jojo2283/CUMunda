package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.repositories.AnswerRepository;
import com.example.workflow.repositories.AssignmentRepository;
import com.example.workflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CheckAnswer implements JavaDelegate {

    private final AssignmentRepository assignmentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));
        Long answerId = (Long) delegateExecution.getVariable("answer_id");

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NoSuchAssigmentException("Answer not found!"));
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new NoSuchAssigmentException("Assignment not found!"));

        boolean isCorrect = answer.getAnswerText().trim().equalsIgnoreCase(assignment.getCorrectAnswer().trim());


        answer.setIsCorrect(isCorrect);
        answer.setNeedVerify(Boolean.FALSE);

        userRepository.save(answer.getUser());
        answerRepository.save(answer);


        delegateExecution.setVariable("correct", answer.getIsCorrect());
        System.out.println("Ответ проверен и сохранен!");

    }
}
