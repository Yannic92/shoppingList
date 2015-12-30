package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.registration.service.ConfirmationMailService;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.security.service.PasswordGenerator;
import de.yannicklem.shoppinglist.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.exception.NotFoundException;
import de.yannicklem.shoppinglist.restutils.service.EntityService;

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
    private final ShoppingListService shoppingListService;
    private final ItemService itemService;
    private final ArticleService articleService;
    private final SLUserValidationService slUserValidationService;
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

        handleBeforeUpdate(slUser);

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


    public void handleBeforeUpdate(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found", slUser.getUsername()));
        }

        if (emailExists(slUser.getEmail()) && !findById(slUser.getUsername()).getEmail().equals(slUser.getEmail())) {
            throw new AlreadyExistsException(String.format("E-mail address '%s' already exists", slUser.getEmail()));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        slUser.setPassword(encoder.encode(slUser.getPassword()));
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

        List<ShoppingList> listsOwnedByUserToDelete = shoppingListService.findListsOwnedBy(slUser);

        for (ShoppingList shoppingList : listsOwnedByUserToDelete) {
            if (shoppingList.getOwners().contains(slUser)) {
                shoppingList.getOwners().remove(slUser);
                shoppingListService.update(shoppingList);
            }
        }

        List<Item> itemsOwnedByUserToDelete = itemService.findItemsOwnedBy(slUser);

        for (Item item : itemsOwnedByUserToDelete) {
            if (item.getOwners().contains(slUser)) {
                item.getOwners().remove(slUser);

                if (item.getOwners().isEmpty()) {
                    itemService.delete(item);
                } else {
                    itemService.update(item);
                }
            }
        }

        List<Article> articlesOwnedByUserToDelete = articleService.findArticlesOwnedBy(slUser);

        for (Article article : articlesOwnedByUserToDelete) {
            if (article.getOwners().contains(slUser)) {
                article.getOwners().remove(slUser);
                articleService.update(article);
            }
        }
    }


    @Override
    public void deleteAll() {

        List<SLUser> all = findAll();

        for (SLUser slUser : all) {
            delete(slUser);
        }
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

        return usernameExists(username);
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
