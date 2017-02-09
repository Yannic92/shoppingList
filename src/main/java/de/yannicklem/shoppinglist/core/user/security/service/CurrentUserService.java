package de.yannicklem.shoppinglist.core.user.security.service;

import de.yannicklem.shoppinglist.core.exception.UnauthorizedException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;

import javax.servlet.http.HttpSession;


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


    public void invalidateCurrentUsersSession() {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }
}
