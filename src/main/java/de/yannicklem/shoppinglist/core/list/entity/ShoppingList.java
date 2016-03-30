package de.yannicklem.shoppinglist.core.list.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yannicklem.shoppinglist.core.OwnedRestEntity;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
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
public class ShoppingList extends OwnedRestEntity<Long> {

    @Id
    @GeneratedValue(generator = "useExistingOrGenerate")
    @GenericGenerator(
        name = "useExistingOrGenerate",
        strategy = "de.yannicklem.shoppinglist.core.user.persistence.UseExistingOrGenerateIdGenerator"
    )
    private Long entityId;

    @ManyToMany
    private final Set<SLUser> owners;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private final Set<Item> items;

    public ShoppingList() {

        this.owners = new HashSet<>();
        this.items = new HashSet<>();
    }

    @Override
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
