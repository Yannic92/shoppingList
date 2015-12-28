package de.yannicklem.shoppinglist.core.list.entity;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.entity.RestEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class ShoppingList implements RestEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private final Set<SLUser> owners;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private final Set<Item> items;

    public ShoppingList() {

        this.owners = new HashSet<>();
        this.items = new HashSet<>();
    }

    public void setOwners(Set<SLUser> owners) {

        this.owners.clear();

        if (owners != null) {
            this.owners.addAll(owners);
        }
    }


    public void setItems(Set<Item> items) {

        this.items.clear();

        if (items != null) {
            this.items.addAll(items);
        }
    }
}
