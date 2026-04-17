package com.thc.capstone.repository;

import com.thc.capstone.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
