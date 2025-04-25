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
public class SendAnswer implements JavaDelegate {

    private final AssignmentRepository assignmentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long assignmentId = Long.parseLong((String) delegateExecution.getVariable("task_id"));
        String answertext = (String) delegateExecution.getVariable("answer_text");

        Answer answer = new Answer();
        answer.setAnswerText(answertext);
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new NoSuchAssigmentException("Assignment not found!"));
        answer.setAssignment(assignment);

        delegateExecution.setVariable("isNeedToVerify", assignment.isRequiresManualReview());
        answer.setNeedVerify(assignment.isRequiresManualReview());


        String userId = delegateExecution.getProcessEngineServices()
                .getIdentityService()
                .getCurrentAuthentication()
                .getUserId();


        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));


        answer.setUser(user);

        userRepository.save(answer.getUser());
        answerRepository.save(answer);
        delegateExecution.setVariable("answer_id", answer.getId());
        System.out.println("Sending answer!");
    }
}
