package de.yannicklem.shoppinglist.core.user.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.registration.service.ConfirmationMailService;
import de.yannicklem.shoppinglist.core.user.restapi.SLUserPermissionEvaluator;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.security.service.PasswordGenerator;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.restutils.EntityService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import javax.servlet.http.HttpSession;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class SLUserService implements UserDetailsService, EntityService<SLUser, String> {

    private final SLUserRepository slUserRepository;
    private final SLUserValidationService slUserValidationService;
    private final SLUserPermissionEvaluator slUserPermissionEvaluator;
    private final CurrentUserService currentUserService;
    private final ConfirmationMailService confirmationMailService;

    @Override
    public SLUser create(SLUser slUser) {

        handleBeforeCreate(slUser);

        SLUser created = slUserRepository.save(slUser);

        handleAfterCreate(created);

        return created;
    }


    @Override
    public SLUser update(SLUser slUser) {

        handleBeforeSave(slUser);

        return slUserRepository.save(slUser);
    }


    public void handleBeforeCreate(SLUser slUser) {

        slUserValidationService.validate(slUser);

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


    public void handleAfterCreate(SLUser slUser) {

        if (!slUser.isEnabled()) {
            confirmationMailService.sendConfirmationMailTo(slUser);
        }
    }


    public void handleBeforeSave(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found", slUser.getUsername()));
        }

        if (emailExists(slUser.getEmail()) && !findById(slUser.getUsername()).getEmail().equals(slUser.getEmail())) {
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

        if (name == null) {
            return false;
        }

        return slUserRepository.exists(name);
    }


    public boolean emailExists(String email) {

        return slUserRepository.findByEmail(email) != null;
    }


    @Override
    public void delete(SLUser slUser) {

        handleBeforeDelete(slUser);
        slUserRepository.delete(slUser);
        handleAfterCelete(slUser);
    }


    public void handleBeforeDelete(SLUser slUser) {

        if (slUser == null || !usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found",
                    slUser == null ? null : slUser.getUsername()));
        }

        slUserValidationService.validate(slUser);
    }


    public void handleAfterCelete(SLUser slUser) {

        SLUser currentUser = currentUserService.getCurrentUser();

        if (currentUser != null && currentUser.equals(slUser)) {
            invalidateDeletedUsersSession();
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SLUser user = slUserRepository.findOne(username);

        if (user == null) {
            user = slUserRepository.findByEmail(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
        }

        return user;
    }


    @Override
    public List<SLUser> findAll() {

        return slUserRepository.findAll();
    }


    @Override
    public boolean exists(String username) {

        return slUserRepository.exists(username);
    }


    @Override
    public SLUser findById(String name) {

        return slUserRepository.findOne(name);
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
