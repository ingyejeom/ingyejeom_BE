package com.thc.capstone.repository;

import com.thc.capstone.domain.Permissiondetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissiondetailRepository extends JpaRepository<Permissiondetail, Long> {
    Permissiondetail findByPermissionIdAndTargetAndFunc(Long permissionId, String target, Integer func);
}
