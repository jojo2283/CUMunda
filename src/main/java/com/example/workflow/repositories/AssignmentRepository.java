package com.example.workflow.repositories;


import com.example.workflow.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
//    List<Assignment> findByCourseId(Long courseId);
}