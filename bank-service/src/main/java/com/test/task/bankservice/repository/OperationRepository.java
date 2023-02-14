package com.test.task.bankservice.repository;

import com.test.task.bankservice.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    Optional<String> findStatusById(UUID billUuid);
}
