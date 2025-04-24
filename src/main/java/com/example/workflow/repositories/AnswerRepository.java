package com.example.workflow.repositories;



import com.example.workflow.entities.Answer;
import com.example.workflow.entities.Assignment;
import com.example.workflow.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
//    List<Answer> findByAssignmentId(Long assignmentId);

//    Page<Answer> findByNeedVerifyTrue(Pageable pageable);

    boolean existsByUserAndAssignmentAndIsCorrectTrue(User user, Assignment assignment);
}
