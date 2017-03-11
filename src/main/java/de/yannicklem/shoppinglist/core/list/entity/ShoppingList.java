package de.yannicklem.shoppinglist.core.list.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.core.Relation;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "entityId", callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Relation(collectionRelation = "shoppingLists")
public class ShoppingList extends OwnedRestEntity<String> {

    @Id
    private String entityId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private final Set<SLUser> owners;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private final Set<Item> items;

    public ShoppingList() {

        this.owners = new HashSet<>();
        this.items = new HashSet<>();
    }

    @JsonProperty
    public Set<SLUser> getOwners() {

        return this.owners;
    }

    @Override
    @JsonIgnore
    public void setOwners(Set<SLUser> owners) {

        this.owners.clear();

        if (owners != null) {
            this.owners.addAll(owners);
        }
    }

    @JsonProperty
    public Set<Item> getItems() {

        return this.items;
    }

    @JsonIgnore
    public void setItems(Set<Item> items) {

        this.items.clear();

        if (items != null) {
            this.items.addAll(items);
        }
    }
}
