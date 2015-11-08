package de.yannicklem.shoppinglist.core.list.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class ShoppingList {

    @Id
    @GeneratedValue
    private Integer id;

    @JsonIgnore
    @ManyToMany
    private final Set<SLUser> owners;

    
    private String name;
    
    @OneToMany
    private final Set<Item> items;

    public ShoppingList() {
        this.owners = new HashSet<>();
        this.items = new HashSet<>();
    }
    
    private void setOwners(Set<SLUser> owners){
        
        this.owners.clear();
        
        if(owners != null){
            this.owners.addAll(owners);
        }
    }
    
    private void setItems(Set<Item> items){
        
        this.items.clear();
        
        if(items != null){
            this.items.addAll(items);
        }
    }
}
