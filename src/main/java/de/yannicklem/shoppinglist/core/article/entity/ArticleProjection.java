package de.yannicklem.shoppinglist.core.article.entity;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "default", types = Article.class)
public interface ArticleProjection {

    String getName();


    double getPriceInEuro();
}
