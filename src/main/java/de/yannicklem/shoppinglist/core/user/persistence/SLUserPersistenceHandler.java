package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.restutils.entity.service.EntityPersistenceHandler;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.exception.AlreadyExistsException;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import de.yannicklem.shoppinglist.core.user.registration.service.ConfirmationMailService;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;
import de.yannicklem.shoppinglist.core.user.validation.SLUserValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Service
public class SLUserPersistenceHandler implements EntityPersistenceHandler<SLUser> {

    private static final Logger LOGGER = LoggerFactory.getLogger(lookup().lookupClass());
    private final SLUserValidationService slUserValidationService;
    private final SLUserReadOnlyService slUserReadOnlyService;
    private final ConfirmationMailService confirmationMailService;
    private final ShoppingListService shoppingListService;
    private final ItemService itemService;
    private final ArticleService articleService;
    private final CurrentUserService currentUserService;

    public SLUserPersistenceHandler(SLUserValidationService slUserValidationService,
                                    @Qualifier("readOnlySLUserService") SLUserReadOnlyService slUserReadOnlyService,
                                    ConfirmationMailService confirmationMailService,
                                    ShoppingListService shoppingListService, ItemService itemService,
                                    ArticleService articleService, CurrentUserService currentUserService) {

        this.slUserValidationService = slUserValidationService;
        this.slUserReadOnlyService = slUserReadOnlyService;
        this.confirmationMailService = confirmationMailService;
        this.shoppingListService = shoppingListService;
        this.itemService = itemService;
        this.articleService = articleService;
        this.currentUserService = currentUserService;
    }

    @Override
    public void handleBeforeCreate(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (slUserReadOnlyService.usernameExists(slUser.getUsername())) {
            throw new AlreadyExistsException(String.format("Username '%s' already exists", slUser.getUsername()));
        }

        if (slUserReadOnlyService.emailExists(slUser.getEmail())) {
            throw new AlreadyExistsException(String.format("E-mail address '%s' already exists", slUser.getEmail()));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        slUser.setPassword(encoder.encode(slUser.getPassword()));

        Confirmation confirmation = new Confirmation();
        confirmation.setCode(UUID.randomUUID().toString());
        slUser.setConfirmation(confirmation);
    }

    @Override
    public void handleAfterCreate(SLUser slUser) {

        if (!slUser.isEnabled()) {
            confirmationMailService.sendConfirmationMailTo(slUser);
        }

        LOGGER.info("Created user: " + slUser.getUsername());
    }

    @Override
    public void handleBeforeUpdate(SLUser slUser) {

        slUserValidationService.validate(slUser);

        if (!slUserReadOnlyService.usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found", slUser.getUsername()));
        }

        SLUser existinguser = slUserReadOnlyService.findById(slUser.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        if (slUserReadOnlyService.emailExists(slUser.getEmail()) && !existinguser.getEmail().equals(slUser.getEmail())) {
            throw new AlreadyExistsException(String.format("E-mail address '%s' already exists", slUser.getEmail()));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        slUser.setPassword(encoder.encode(slUser.getPassword()));
    }

    @Override
    public void handleAfterUpdate(SLUser entity) {

        LOGGER.info("Updated user: " + entity.getUsername());
    }

    @Override
    public void handleBeforeDelete(SLUser slUser) {

        if (slUser == null || !slUserReadOnlyService.usernameExists(slUser.getUsername())) {
            throw new NotFoundException(String.format("User '%s' not found",
                    slUser == null ? null : slUser.getUsername()));
        }

        slUserValidationService.validate(slUser);

        List<ShoppingList> listsOwnedByUserToDelete = shoppingListService.findListsOwnedBy(slUser);

        for (ShoppingList shoppingList : listsOwnedByUserToDelete) {
            removeOwner(shoppingList, this.shoppingListService, slUser);
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

    private void removeOwner(OwnedRestEntity entity, EntityService entityService, SLUser ownerToRemove) {

        if (entity.getOwners().contains(ownerToRemove)) {
            entity.getOwners().remove(ownerToRemove);

            if (entity.getOwners().isEmpty()) {
                entityService.delete(entity);
            } else {
                entityService.update(entity);
            }
        }
    }

    @Override
    public void handleAfterDelete(SLUser slUser) {

        SLUser currentUser = currentUserService.getCurrentUser();

        if (currentUser != null && currentUser.equals(slUser)) {
            currentUserService.invalidateCurrentUsersSession();
        }

        LOGGER.info("Deleted user: " + slUser.getUsername());
    }
}
