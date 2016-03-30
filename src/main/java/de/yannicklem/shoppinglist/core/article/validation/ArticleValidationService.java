package de.yannicklem.shoppinglist.core.article.validation;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.validation.SLUserValidationService;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ArticleValidationService {

    private final SLUserValidationService slUserValidationService;

    public void validate(Article article) throws EntityInvalidException {

        if (article == null) {
            throw new EntityInvalidException("Article must not be null");
        }

        validateName(article.getName());

        if (article.getPriceInEuro() < 0) {
            throw new EntityInvalidException("Article price must be >=0");
        }

        validateOwners(article.getOwners());
    }


    private void validateOwners(Set<SLUser> owners) {

        if (owners == null || owners.isEmpty()) {
            throw new EntityInvalidException("Owners must not be null or empty");
        }

        owners.forEach(slUserValidationService::validate);
    }


    private void validateName(String name) {

        if (name == null || name.isEmpty()) {
            throw new EntityInvalidException("Der Name eines Artikels darf nicht leer sein");
        }

        if (name.length() > 140) {
            throw new EntityInvalidException("Der Name eines Artikels darf nicht mehr als 140 Zeichen enthalten");
        }
    }
}
