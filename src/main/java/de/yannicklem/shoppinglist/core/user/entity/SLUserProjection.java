package de.yannicklem.shoppinglist.core.user.entity;

import java.util.Set;


public interface SLUserProjection {

    String getUsername();


    String getEmail();

    String getFirstName();
    
    String getLastName();

    Set<SLAuthority> getAuthorities();
}
