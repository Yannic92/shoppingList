package de.yannicklem.shoppinglist.core.user.service;

import de.yannicklem.shoppinglist.core.user.SLUser;
import de.yannicklem.shoppinglist.core.user.SLUserRepository;
import de.yannicklem.shoppinglist.core.user.registration.Confirmation;
import de.yannicklem.shoppinglist.core.user.registration.ConfirmationMailService;
import de.yannicklem.shoppinglist.core.user.security.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.security.SLUserPermissionEvaluator;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.exception.PermissionDeniedException;
import de.yannicklem.shoppinglist.security.PasswordGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;


@Service
@RepositoryEventHandler(SLUser.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserService implements UserDetailsService {

    private final SLUserRepository slUserRepository;
    private final SLUserValidationService slUserValidationService;
    private final SLUserPermissionEvaluator slUserPermissionEvaluator;
    private final CurrentUserService currentUserService;
    private final ConfirmationMailService confirmationMailService;

    public SLUser create(SLUser slUser) {

        handleBeforeCreateSLUser(slUser);

        SLUser created = slUserRepository.save(slUser);

        handleAfterCreate(created);

        return created;
    }


    @HandleBeforeCreate(SLUser.class)
    public void handleBeforeCreateSLUser(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!slUserPermissionEvaluator.currentUserIsAllowedToCreateUser(slUser)) {
            throw new PermissionDeniedException("User is not allowed to create a new admin");
        }

        if (usernameExists(slUser.getUsername())) {
            throw new AlreadyExistsException(String.format("Username '%s' already exists", slUser.getUsername()));
        }

        if (emailExists(slUser.getEmail())) {
            throw new AlreadyExistsException(String.format("E-mail address '%s' already exists", slUser.getEmail()));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        slUser.setPassword(encoder.encode(slUser.getPassword()));

        Confirmation confirmation = new Confirmation();
        confirmation.setCode(PasswordGenerator.generatePassword());
        slUser.setConfirmation(confirmation);
    }


    @HandleAfterCreate(SLUser.class)
    public void handleAfterCreate(SLUser slUser) {

        if (!slUser.isEnabled()) {
            confirmationMailService.sendConfirmationMailTo(slUser);
        }
    }


    @HandleBeforeSave(SLUser.class)
    public void handleBeforeSave(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!slUserPermissionEvaluator.currentUserIsAllowedToUpdateUser(slUser)) {
            throw new PermissionDeniedException(String.format("User is not allowed to update user '%s'",
                    slUser.getUsername()));
        }

        if (!usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found", slUser.getUsername()));
        }

        if (emailExists(slUser.getEmail()) && !findByName(slUser.getUsername()).getEmail().equals(slUser.getEmail())) {
            throw new AlreadyExistsException(String.format("E-mail address '%s' already exists", slUser.getEmail()));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        slUser.setPassword(encoder.encode(slUser.getPassword()));

        Confirmation confirmation = new Confirmation();
        confirmation.setCode(PasswordGenerator.generatePassword());
        slUser.setConfirmation(confirmation);
    }


    private void invalidateDeletedUsersSession() {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }


    public boolean usernameExists(String name) {

        return slUserRepository.exists(name);
    }


    public boolean emailExists(String email) {

        return slUserRepository.findByEmail(email) != null;
    }


    public void delete(SLUser slUser) {

        handleBeforeDelete(slUser);
        slUserRepository.delete(slUser);
        handleAfterCelete(slUser);
    }


    @HandleBeforeDelete(SLUser.class)
    public void handleBeforeDelete(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!slUserPermissionEvaluator.currentUserIsAllowedToDeleteUser(slUser)) {
            throw new PermissionDeniedException(String.format("User is not allowed to delete user '%s'",
                    slUser.getUsername()));
        }

        if (!usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found", slUser.getUsername()));
        }
    }


    @HandleAfterDelete(SLUser.class)
    public void handleAfterCelete(SLUser slUser) {

        SLUser currentUser = currentUserService.getCurrentUser();

        if (currentUser != null && currentUser.equals(slUser)) {
            invalidateDeletedUsersSession();
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SLUser user = slUserRepository.findOne(username);

        if(user == null){
            user = slUserRepository.findByEmail(username);
        }
        
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
        }

        return user;
    }


    public List<SLUser> findAll() {

        List<SLUser> all = slUserRepository.findAll();

        if (all != null && !all.isEmpty()) {
            all = filterAfterRead(all);
        }

        return all;
    }


    private List<SLUser> filterAfterRead(List<SLUser> slUsers) {

        List<SLUser> filtered = new ArrayList<>();

        for (SLUser slUser : slUsers) {
            if (slUserPermissionEvaluator.currentUserIsAllowedToReadUser(slUser)) {
                filtered.add(slUser);
            }
        }

        return filtered;
    }


    public SLUser findByName(String name) {

        SLUser slUser = slUserRepository.findOne(name);

        if (slUserPermissionEvaluator.currentUserIsAllowedToReadUser(slUser)) {
            return slUser;
        }

        throw new PermissionDeniedException("Access denied");
    }


    public void confirmUserRegistration(Confirmation confirmation, String name) {

        SLUser slUser = slUserRepository.findOne(name);

        if (slUser == null || slUser.isEnabled()) {
            throw new NotFoundException(String.format("User '%s' not found", name));
        }

        if (slUser.getConfirmation().getCode().equals(confirmation.getCode())) {
            slUser.setEnabled(true);
            slUserRepository.save(slUser);
        } else {
            throw new EntityInvalidException("Bestätigungscode nicht korrekt");
        }
    }
}
