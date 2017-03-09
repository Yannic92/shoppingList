package de.yannicklem.shoppinglist.core.article;

import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.core.Relation;

import javax.persistence.Entity;
import java.util.Set;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "name", callSuper = false)
@ToString
@Relation(collectionRelation = "articles")
public class Article extends OwnedRestEntity<String> {

    private String name;
    private double priceInEuro;

    public Article(String name, double priceInEuro, Set<SLUser> owners) {

        this();
        this.name = name;
        this.priceInEuro = priceInEuro;
        setOwners(owners);
    }


    public Article(Article article) {

        this();
        this.name = article.getName();
        this.priceInEuro = article.getPriceInEuro();
        setOwners(article.getOwners());
    }


    public Article() {

        this.name = "";
        this.priceInEuro = 0;
    }
}
