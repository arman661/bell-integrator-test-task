package com.test.task.bankservice.repository;

import com.test.task.bankservice.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    Optional<Bill> findStatusById(UUID billUuid);
}
