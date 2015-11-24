package de.yannicklem.shoppinglist.core.user.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class SLUserDetailed {

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
}
