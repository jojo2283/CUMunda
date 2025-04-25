package com.example.workflow.component;

import com.example.workflow.entities.Answer;
import com.example.workflow.entities.User;
import com.example.workflow.kafka.EmailRequest;
import com.example.workflow.kafka.KafkaProducer;
import com.example.workflow.repositories.AnswerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class SuccessMessage implements JavaDelegate {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final AnswerRepository answerRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Long answerId = (Long) delegateExecution.getVariable("answer_id");
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("Answer not found"));

        User user = answer.getUser();
        try {
            String json = objectMapper.writeValueAsString(new EmailRequest(
                    user.getEmail(),
                    "Навык коробка",
                    "Вы получили " + answer.getAssignment().getPoints() + " баллов за верно выполненное задание."

            ));
            kafkaProducer.sendMessage(json);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException();
        }
    }
}
