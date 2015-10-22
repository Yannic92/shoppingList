package de.yannicklem.shoppinglist.core.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Getter
@Table(name = "AUTHORITIES")
@EqualsAndHashCode
@ToString
public class SLAuthority implements GrantedAuthority {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String[] VALID_AUTHORITIES = { ADMIN, USER };

    @Id
    private String authority;

    public SLAuthority() {
    }


    public SLAuthority(String authority) {

        this.authority = authority;
    }

    @Override
    public String getAuthority() {

        return authority;
    }
}
