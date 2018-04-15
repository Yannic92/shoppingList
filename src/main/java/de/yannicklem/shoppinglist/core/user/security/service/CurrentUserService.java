package de.yannicklem.shoppinglist.core.user.security.service;

import coffee.synyx.autoconfigure.security.service.CoffeeNetCurrentUserService;
import coffee.synyx.autoconfigure.security.service.CoffeeNetUserDetails;
import de.yannicklem.shoppinglist.core.exception.UnauthorizedException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpSession;


@Service
public class CurrentUserService {

    private final CoffeeNetCurrentUserService coffeeNetCurrentUserService;

    @Autowired
    public CurrentUserService(CoffeeNetCurrentUserService coffeeNetCurrentUserService) {
        this.coffeeNetCurrentUserService = coffeeNetCurrentUserService;
    }

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

        Optional<CoffeeNetUserDetails> coffeeNetUserDetails = coffeeNetCurrentUserService.get();

        if(coffeeNetUserDetails.isPresent()){
            return SLUser.fromCoffeeNetUserDetails(coffeeNetUserDetails.get());
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
