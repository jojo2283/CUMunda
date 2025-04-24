package com.example.workflow.services;



import com.example.workflow.entities.Assignment;
import com.example.workflow.exception.NoSuchAssigmentException;
import com.example.workflow.exception.NoSuchCourseException;
import com.example.workflow.repositories.AssignmentRepository;
import com.example.workflow.repositories.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class AssignmentService {

    private final TransactionTemplate transactionTemplate;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(PlatformTransactionManager transactionManager, AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setTimeout(30);
    }


    public List<Assignment> getAllAssignments() {
        return transactionTemplate.execute(status -> assignmentRepository.findAll());

    }


    public Assignment getAssignmentById(Long id) {
        return transactionTemplate.execute(status -> assignmentRepository.findById(id).orElseThrow(() -> new NoSuchAssigmentException("Assignment not found")));


    }


    public Assignment createAssignment(Assignment assignment) {
        return transactionTemplate.execute(status -> {
            if (assignment.getCourse() != null && assignment.getCourse().getId() != null) {
                courseRepository.findById(assignment.getCourse().getId())
                        .orElseThrow(() -> new NoSuchCourseException("Course not found"));
            }
            return assignmentRepository.save(assignment);
        });
    }


}
