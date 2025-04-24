package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.example.workflow.entities.User;
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
public class AddBonuspoints  implements JavaDelegate {
    private  final AssignmentRepository assignmentRepository;
    private  final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long answerId = Long.parseLong((String) delegateExecution.getVariable("answer_id"));
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));

        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(()->new NoSuchAssigmentException("Assignment not found!"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(()->new NoSuchAssigmentException("Answer not found!"));

        User user = answer.getUser();
        user.setPoints(user.getPoints() + assignment.getPoints());
        userRepository.save(user);
        System.out.println("Adding bonuspoints");
    }
}
