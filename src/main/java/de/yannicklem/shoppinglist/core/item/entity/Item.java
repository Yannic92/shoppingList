package de.yannicklem.shoppinglist.core.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.article.Article;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.core.Relation;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "entityId", callSuper = false)
@Relation(collectionRelation = "items")
public class Item extends OwnedRestEntity<String> {

    private boolean done;

    private String count;

    @ManyToOne(
        optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }
    )
    private Article article;

    @JsonIgnore
    private Date createdAt;

    public Item() {

        this.createdAt = new Date();
        done = false;
    }


    public Item(Item item) {

        this();
        this.count = item.getCount();
        this.article = item.getArticle();
        setOwners(item.getOwners());
    }


    public Item(Article article, String count, Set<SLUser> owners) {

        this();
        this.article = article;
        this.count = count;
        setOwners(owners);
    }

    @JsonIgnore
    public void setCreatedAt(Date createdAt) {

        if (createdAt == null) {
            createdAt = new Date();
        }

        this.createdAt = createdAt;
    }
}
