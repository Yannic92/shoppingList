package de.yannicklem.shoppinglist.core.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.yannicklem.shoppinglist.core.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;

import org.springframework.hateoas.core.Relation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;


@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "entityId", callSuper = false)
@Relation(collectionRelation = "items")
public class Item extends OwnedRestEntity<Long> {

    @Id
    @GeneratedValue(generator = "useExistingOrGenerate")
    @GenericGenerator(
        name = "useExistingOrGenerate",
        strategy = "de.yannicklem.shoppinglist.core.persistence.UseExistingOrGenerateIdGenerator"
    )
    private Long entityId;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SLUser> owners;

    private boolean done;

    private Integer count;

    @ManyToOne(
        optional = false, fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH
        }
    )
    private Article article;

    public Item() {

        this.owners = new HashSet<>();
        done = false;
    }


    public Item(Item item) {

        this();
        this.count = item.getCount();
        this.article = item.getArticle();
        setOwners(item.getOwners());
    }


    public Item(Article article, Integer count, Set<SLUser> owners) {

        this();
        this.article = article;
        this.count = count;
        setOwners(owners);
    }

    @Override
    public void setOwners(Set<SLUser> owners) {

        this.owners.clear();

        if (owners != null) {
            this.owners.addAll(owners);
        }
    }
}
