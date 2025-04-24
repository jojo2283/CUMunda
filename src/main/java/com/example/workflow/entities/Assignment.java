package com.example.workflow.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "assignments")
@RequiredArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private boolean requiresManualReview;

    private String correctAnswer;

    private int points;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;


}
