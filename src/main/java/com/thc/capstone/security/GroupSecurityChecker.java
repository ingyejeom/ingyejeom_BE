package com.thc.capstone.security;

import com.thc.capstone.mapper.UserSpaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupSecurityChecker {

    private final UserSpaceMapper userSpaceMapper;

    public boolean isGroupAdmin(Long groupId) {
        log.info("[GroupSecurityChecker] isGroupAdmin called with groupId: {}", groupId);
        
        if (groupId == null) {
            log.info("[GroupSecurityChecker] groupId is null. Returning false.");
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.info("[GroupSecurityChecker] User not authenticated. Returning false.");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {
            Long userId = ((PrincipalDetails) principal).getUser().getId();
            log.info("[GroupSecurityChecker] User authenticated. userId: {}", userId);
            
            boolean result = userSpaceMapper.isGroupAdmin(Map.of("userId", userId, "groupId", groupId));
            log.info("[GroupSecurityChecker] isGroupAdmin query result: {}", result);
            
            return result;
        }
        
        log.info("[GroupSecurityChecker] Principal is not PrincipalDetails. Returning false.");
        return false;
    }
}
