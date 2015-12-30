package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemValidationService {

    private final ArticleValidationService articleValidationService;
    private final SLUserValidationService slUserValidationService;

    public void validate(Item item) throws EntityInvalidException {

        if (item == null) {
            throw new EntityInvalidException("Item must not be null");
        }

        validateOwners(item.getOwners());

        articleValidationService.validate(item.getArticle());

        if (item.getCount() <= 0) {
            throw new EntityInvalidException("Count must be >0");
        }
    }


    private void validateOwners(Set<SLUser> owners) {

        if (owners == null || owners.isEmpty()) {
            throw new EntityInvalidException("Owners must not be null or empty");
        }

        owners.forEach(slUserValidationService::validate);
    }
}
