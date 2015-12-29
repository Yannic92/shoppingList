package de.yannicklem.shoppinglist.core.article.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.yannicklem.shoppinglist.core.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "entityId", callSuper = false)
@ToString
public class Article extends OwnedRestEntity<Long> {

    @Id
    @GeneratedValue
    private Long entityId;

    private String name;
    private double priceInEuro;

    @JsonIgnore
    @ManyToMany
    private final Set<SLUser> owners;

    public Article(String name, double priceInEuro, Set<SLUser> owners) {

        this();
        this.name = name;
        this.priceInEuro = priceInEuro;
        setOwners(owners);
    }


    public Article() {

        this.name = "";
        this.priceInEuro = 0;
        this.owners = new HashSet<>();
    }

    @Override
    public void setOwners(Set<SLUser> owners) {

        this.owners.clear();

        if (owners != null) {
            this.owners.addAll(owners);
        }
    }
}
