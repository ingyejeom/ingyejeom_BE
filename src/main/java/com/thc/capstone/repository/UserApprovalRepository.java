package com.thc.capstone.repository;

import com.thc.capstone.domain.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserApprovalRepository extends JpaRepository<UserApproval, Long> {
    List<UserApproval> findAllByApprovalId(Long approvalId);
}
