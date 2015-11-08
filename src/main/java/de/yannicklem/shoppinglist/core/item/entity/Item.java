package de.yannicklem.shoppinglist.core.item.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.shoppinglist.core.article.entity.Article;
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
public class Item{

    @Id
    @GeneratedValue
    private Integer id;

    @JsonIgnore
    @ManyToMany
    private final Set<SLUser> owners;
    
    private Integer count;
    
    @ManyToOne(optional = false)
    private Article article;

    public Item() {
        this.owners = new HashSet<>();
    }

    private void setOwners(Set<SLUser> owners){

        this.owners.clear();

        if(owners != null){
            this.owners.addAll(owners);
        }
    }
}
