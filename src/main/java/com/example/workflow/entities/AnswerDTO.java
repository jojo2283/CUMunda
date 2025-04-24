package com.example.workflow.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    private Long id;
    private String answerText;
    private Boolean isCorrect;
    private Long assignmentId;
    private String message;
    private Boolean needVerify;

    public AnswerDTO(Long id, String answerText, Boolean isCorrect, String message,Long assignmentId,Boolean needVerify) {
        this.id = id;
        this.message = message;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.assignmentId = assignmentId;
        this.needVerify = needVerify;
    }


}
