package de.yannicklem.shoppinglist.core.user.restapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.hateoas.core.Relation;


@Relation(collectionRelation = "sLUsers")
public class SLUserDetailed extends SLUser {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    public SLUserDetailed(SLUser slUser) {

        this.username = slUser.getUsername();
        this.firstName = slUser.getFirstName();
        this.lastName = slUser.getLastName();
        this.email = slUser.getEmail();
    }

    @Override
    @JsonIgnore(false)
    public String getUsername() {

        return username;
    }


    @Override
    @JsonIgnore(false)
    public String getFirstName() {

        return firstName;
    }


    @Override
    @JsonIgnore(false)
    public String getLastName() {

        return lastName;
    }


    @Override
    @JsonIgnore(false)
    public String getEmail() {

        return email;
    }
}
