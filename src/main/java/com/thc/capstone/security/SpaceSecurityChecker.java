package com.thc.capstone.security;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.UserSpaceStatus;
import com.thc.capstone.repository.UserSpaceRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpaceSecurityChecker {

    private final UserSpaceRepository userSpaceRepository;

    public SpaceSecurityChecker(UserSpaceRepository userSpaceRepository) {
        this.userSpaceRepository = userSpaceRepository;
    }

    public boolean isMember(Long spaceId, Long userId) {
        System.out.println("[SpaceSecurityChecker] isMember called with spaceId: " + spaceId + ", userId: " + userId);
        if (spaceId == null || userId == null) {
            System.out.println("[SpaceSecurityChecker] spaceId or userId is null. Returning false.");
            return false;
        }
        
        List<UserSpaceStatus> validStatuses = List.of(UserSpaceStatus.ACTIVE, UserSpaceStatus.PENDING);
        List<Role> validRoles = List.of(Role.ADMIN, Role.USER);
        
        boolean result = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatusInAndRoleInAndDeleted(
                userId, spaceId, validStatuses, validRoles, false
        ).isPresent();
        
        System.out.println("[SpaceSecurityChecker] findFirstByUserIdAndSpaceIdAndStatusInAndRoleInAndDeleted returned: " + result);
        return result;
    }
    
    public boolean isMember(Long spaceId) {
        System.out.println("[SpaceSecurityChecker] isMember(Long spaceId) called with spaceId: " + spaceId);
        if (spaceId == null) {
            System.out.println("[SpaceSecurityChecker] spaceId is null. Returning false.");
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("[SpaceSecurityChecker] User not authenticated. Returning false.");
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {
            Long userId = ((PrincipalDetails) principal).getUser().getId();
            System.out.println("[SpaceSecurityChecker] User authenticated. userId: " + userId);
            return isMember(spaceId, userId);
        }
        System.out.println("[SpaceSecurityChecker] Principal is not PrincipalDetails. Returning false.");
        return false;
    }
}
