package com.thc.capstone.repository;

import com.thc.capstone.domain.Permissionuser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionuserRepository extends JpaRepository<Permissionuser, Long> {
    Permissionuser findByPermissionIdAndUserId(Long permissionId, Long userId);
}