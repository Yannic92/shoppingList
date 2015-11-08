package de.yannicklem.shoppinglist.core.item.service;

import de.yannicklem.shoppinglist.core.article.service.ArticleValidationService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class ItemValidationService {

    private final ArticleValidationService articleValidationService;

    public void validate(Item item) throws EntityInvalidException {
    }
}
