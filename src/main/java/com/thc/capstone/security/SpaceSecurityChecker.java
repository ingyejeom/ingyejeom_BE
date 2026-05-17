package com.thc.capstone.security;

import com.thc.capstone.domain.UserSpaceStatus;
import com.thc.capstone.repository.UserSpaceRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpaceSecurityChecker {

    private final UserSpaceRepository userSpaceRepository;

    public SpaceSecurityChecker(UserSpaceRepository userSpaceRepository) {
        this.userSpaceRepository = userSpaceRepository;
    }

    public boolean isMember(Long spaceId, Long userId) {
        if (spaceId == null || userId == null) return false;
        return userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(userId, spaceId, UserSpaceStatus.ACTIVE).isPresent();
    }
    
    public boolean isMember(Long spaceId) {
        if (spaceId == null) return false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {
            Long userId = ((PrincipalDetails) principal).getUser().getId();
            return isMember(spaceId, userId);
        }
        return false;
    }
}
