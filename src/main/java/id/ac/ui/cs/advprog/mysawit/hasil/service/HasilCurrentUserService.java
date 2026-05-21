package id.ac.ui.cs.advprog.mysawit.hasil.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class HasilCurrentUserService {
    public static final String BURUH_ROLE = "ROLE_BURUH";
    public static final String MANDOR_ROLE = "ROLE_MANDOR";

    public String currentBuruhUsername() {
        return currentUsernameForRole(BURUH_ROLE);
    }

    public String currentMandorUsername() {
        return currentUsernameForRole(MANDOR_ROLE);
    }

    private String currentUsernameForRole(String requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        boolean hasRequiredRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> requiredRole.equals(authority.getAuthority()));
        if (!hasRequiredRole) {
            throw new AccessDeniedException("Forbidden");
        }

        return authentication.getName();
    }
}
