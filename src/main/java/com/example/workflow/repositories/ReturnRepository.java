package com.example.workflow.repositories;

import com.example.workflow.entities.ReturnRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnRequestEntity, Long> {
    Page<ReturnRequestEntity> findAll(Pageable pageable);
}
