package de.yannicklem.shoppinglist.core.article.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@EqualsAndHashCode(of = "id")
@ToString
public class Article {

    @Id
    @GeneratedValue
    private Integer id;
    
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
    

    private void setOwners(Set<SLUser> owners){

        this.owners.clear();

        if(owners != null){
            this.owners.addAll(owners);
        }
    }
}
