package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.shoppinglist.core.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.validation.SLUserValidationService;
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

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
@Transactional
public class SLUserService implements UserDetailsService, EntityService<SLUser, String> {

    private static Logger LOGGER = getLogger(lookup().lookupClass());
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

        LOGGER.info("Created user: " + created.getUsername());

        return created;
    }


    @Override
    public SLUser update(SLUser slUser) {

        handleBeforeUpdate(slUser);

        SLUser updatedUser = slUserRepository.save(slUser);

        LOGGER.info("Updated user: " + updatedUser.getUsername());

        return updatedUser;
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

        if (email == null) {
            return false;
        }

        return slUserRepository.findByEmail(email) != null;
    }


    @Override
    public void delete(SLUser slUser) {

        handleBeforeDelete(slUser);
        slUserRepository.delete(slUser);
        handleAfterCelete(slUser);

        LOGGER.info("Deleted user: " + slUser.getUsername());
    }


    public void handleBeforeDelete(SLUser slUser) {

        if (slUser == null || !usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found",
                    slUser == null ? null : slUser.getUsername()));
        }

        slUserValidationService.validate(slUser);

        List<ShoppingList> listsOwnedByUserToDelete = shoppingListService.findListsOwnedBy(slUser);

        for (ShoppingList shoppingList : listsOwnedByUserToDelete) {
            removeOwner(shoppingList, shoppingListService, slUser);
        }

        List<Item> itemsOwnedByUserToDelete = itemService.findItemsOwnedBy(slUser);

        for (Item item : itemsOwnedByUserToDelete) {
            removeOwner(item, itemService, slUser);
        }

        List<Article> articlesOwnedByUserToDelete = articleService.findArticlesOwnedBy(slUser);

        for (Article article : articlesOwnedByUserToDelete) {
            removeOwner(article, articleService, slUser);
        }
    }

    private void removeOwner(OwnedRestEntity entity, EntityService entityService, SLUser ownerToRemove){
        if(entity.getOwners().contains(ownerToRemove)){
            entity.getOwners().remove(ownerToRemove);

            if( entity.getOwners().isEmpty()){

                entityService.delete(entity);
            }else {

                entityService.update(entity);
            }
        }
    }


    @Override
    public void deleteAll() {

        List<SLUser> all = findAll(currentUserService.getCurrentUser());

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
            user = slUserRepository.findByEmail(username.toLowerCase());
        }

        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username '%s' not found", username));
        }

        return user;
    }


    @Override
    public List<SLUser> findAll(SLUser currentUser) {

        return slUserRepository.findAll();
    }


    @Override
    public boolean exists(String username) {

        if (username == null) {
            return false;
        }

        return usernameExists(username);
    }


    @Override
    public SLUser findById(String name) {

        if (name == null) {
            return null;
        }

        return slUserRepository.findOne(name);
    }


    public void confirmUserRegistration(Confirmation confirmation, String name) {

        SLUser slUser = slUserRepository.findOne(name);

        if (slUser == null || slUser.isEnabled()) {
            throw new EntityInvalidException("Aktivierung nicht möglich");
        }

        if (slUser.getConfirmation().getCode().equals(confirmation.getCode())) {
            slUser.setEnabled(true);
            slUserRepository.save(slUser);
        } else {
            throw new EntityInvalidException("Aktivierung nicht möglich");
        }
    }


    public List<SLUser> findInactiveUsersOlderThan(Date date) {

        return slUserRepository.findInactiveUsersOlderThan(date);
    }
}
