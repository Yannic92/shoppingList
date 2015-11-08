package de.yannicklem.shoppinglist.core.user.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SLUserDetailed {
    
    public SLUserDetailed(SLUser slUser){
        this.username = slUser.getUsername();
        this.firstName = slUser.getFirstName();
        this.lastName = slUser.getLastName();
        this.email = slUser.getEmail();
    }

    private String username;

    private String firstName;

    private String lastName;

    private String email;
}
