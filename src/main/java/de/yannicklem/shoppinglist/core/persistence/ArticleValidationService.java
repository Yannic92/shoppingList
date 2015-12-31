package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
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

        if (article.getName() == null || article.getName().isEmpty()) {
            throw new EntityInvalidException("Article name must not be null or empty");
        }

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
}
