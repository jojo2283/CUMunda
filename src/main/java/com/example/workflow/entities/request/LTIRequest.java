package com.example.workflow.entities.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LTIRequest {

    private String timestamp;

    private String userId;

    private String comment;

    private String activityProgress;

    private String gradingProgress;

}
