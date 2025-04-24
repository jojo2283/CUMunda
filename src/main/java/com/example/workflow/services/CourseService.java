package com.example.workflow.services;


import com.example.workflow.entities.Course;
import com.example.workflow.exception.NoSuchCourseException;
import com.example.workflow.repositories.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service

public class CourseService {
    private final CourseRepository courseRepository;
    private final TransactionTemplate transactionTemplate;

    public CourseService(PlatformTransactionManager transactionManager, CourseRepository courseRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setTimeout(30);
        this.courseRepository = courseRepository;
    }


    public List<Course> getCourses() {
        return transactionTemplate.execute(status -> courseRepository.findAll());
    }


    public Course getCourseById(Long id) {
        return transactionTemplate.execute(status -> courseRepository.findById(id).orElseThrow(() -> new NoSuchCourseException("Course not found")));
    }


    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

//    @Transactional
//    public Course updateCourse(Long id, Course courseDetails) {
//        Course course = courseRepository.findById(id).orElseThrow(() -> new NoSuchCourseException("Course not found"));
//        course.setName(courseDetails.getName());
//        course.setDescription(courseDetails.getDescription());
//        return courseRepository.save(course);
//    }


}
