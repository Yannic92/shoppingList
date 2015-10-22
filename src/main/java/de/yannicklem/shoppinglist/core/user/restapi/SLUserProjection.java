package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.SLAuthority;

import java.util.Set;


public interface SLUserProjection {

    String getUsername();


    String getEmail();


    Set<SLAuthority> getAuthorities();
}
