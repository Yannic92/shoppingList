package de.yannicklem.shoppinglist.core.user.security;

import de.yannicklem.shoppinglist.core.user.SLUser;
import de.yannicklem.shoppinglist.exception.UnauthorizedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.HashSet;


@Service
public class CurrentUserService {

    public boolean currentUserIsAdminOrSystemUser() {

        SLUser currentUser = getCurrentUser();

        return currentUser == null || currentUser.isAdmin();
    }


    public SLUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object currentUser = authentication.getPrincipal();

        if (currentUser instanceof SLUser) {
            return ((SLUser) currentUser);
        }

        if (isAnonymousUser(currentUser)) {
            return getAnonymousUser();
        }

        throw new UnauthorizedException("User was not of type SLUser");
    }


    private boolean isAnonymousUser(Object currentUser) {

        return "anonymousUser".equals(currentUser);
    }


    private SLUser getAnonymousUser() {

        return new SLUser("anonymousUser", "", "", "", "", true, null, new HashSet<>());
    }
}
