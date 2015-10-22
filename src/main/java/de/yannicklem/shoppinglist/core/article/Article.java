package de.yannicklem.shoppinglist.core.article;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.yannicklem.shoppinglist.core.AbstractEntity;
import de.yannicklem.shoppinglist.core.user.SLUser;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
@Getter
@ToString
public class Article extends AbstractEntity {

    private String name;
    private double priceInEuro;

    @JsonIgnore
    @ManyToOne(optional = false)
    private SLUser owner;

    public Article(String name, double priceInEuro, SLUser owner) {

        this.name = name;
        this.priceInEuro = priceInEuro;
        this.owner = owner;
    }


    public Article() {

        this.name = "";
        this.priceInEuro = 0;
    }
}
