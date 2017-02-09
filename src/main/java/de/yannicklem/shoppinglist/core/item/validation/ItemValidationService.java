package de.yannicklem.shoppinglist.core.item.validation;

import de.yannicklem.shoppinglist.core.article.validation.ArticleValidationService;
import de.yannicklem.shoppinglist.core.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.validation.SLUserValidationService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemValidationService {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());
    private final ArticleValidationService articleValidationService;
    private final SLUserValidationService slUserValidationService;

    public void validate(Item item) throws EntityInvalidException {

        if (item == null) {
            throw new EntityInvalidException("Item must not be null");
        }

        try {
            validateOwners(item.getOwners());

            validateCount(item.getCount());

            articleValidationService.validate(item.getArticle());
        } catch (EntityInvalidException entityInvalidException) {
            LOGGER.info("Validation of item (entityId: {}) failed: {}", item.getEntityId(),
                entityInvalidException.getMessage());
            LOGGER.debug("The following exception occurred, during validation of item (entityId: {})",
                entityInvalidException);
            throw entityInvalidException;
        }
    }


    private void validateCount(String count) {

        if (count != null && count.length() > 140) {
            throw new EntityInvalidException("Menge darf maximal 140 Zeichen enthalten");
        }
    }


    private void validateOwners(Set<SLUser> owners) {

        if (owners == null || owners.isEmpty()) {
            throw new EntityInvalidException("Owners must not be null or empty");
        }

        owners.forEach(slUserValidationService::validate);
    }
}
