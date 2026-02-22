package com.ces.contract.repository;

import com.ces.contract.entity.ContractRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<ContractRecord, Long> {
    List<ContractRecord> findAllByOrderByCreatedAtDesc();
}
