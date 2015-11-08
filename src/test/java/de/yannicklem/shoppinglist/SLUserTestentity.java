package de.yannicklem.shoppinglist;


import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SLUserTestEntity {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;
    
    private Set<GrantedAuthority> authorities;

    public SLUserTestEntity(SLUser slUser){
            this.username = slUser.getUsername();
            this.firstName = slUser.getFirstName();
            this.lastName = slUser.getLastName();
            this.email = slUser.getEmail();
            this.password = slUser.getPassword();
            this.authorities = new HashSet<>();
            this.authorities.addAll(slUser.getAuthorities());
    }
}
