package de.yannicklem.shoppinglist.core.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.yannicklem.restutils.entity.RestEntity;

import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.Email;

import org.springframework.hateoas.core.Relation;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Getter
@Setter
@Table(name = "USERS")
@ToString(exclude = "password")
@EqualsAndHashCode(of = { "username" }, callSuper = false)
@Relation(collectionRelation = "sLUsers")
public class SLUser extends RestEntity<String> implements UserDetails {

    @Id
    @Column(unique = true, nullable = false)
    private String username;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ManyToMany(
        fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = SLAuthority.class
    )
    private final Set<GrantedAuthority> authorities;

    @JsonIgnore
    private boolean enabled;

    @JsonIgnore
    private boolean accountNonExpired;

    @JsonIgnore
    private boolean accountNonLocked;

    @JsonIgnore
    private boolean credentialsNonExpired;

    @Email(message = "Keine gueltige E-Mail-Adresse")
    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String email;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JsonIgnore
    private Confirmation confirmation;

    @JsonIgnore
    private Date createdAt;

    public SLUser() {

        authorities = new HashSet<>();
        this.enabled = false;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.createdAt = new Date();
    }


    public SLUser(String username, String firstName, String lastName, String password, String email, boolean enabled,
        Confirmation confirmation, Collection<? extends GrantedAuthority> authorities) {

        this();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.confirmation = confirmation;
        setAuthorities(authorities);
    }

    @Override
    @JsonIgnore
    public String getPassword() {

        return this.password;
    }


    @JsonProperty
    public void setPassword(String password) {

        this.password = password;
    }


    @Override
    @JsonIgnore
    public Set<GrantedAuthority> getAuthorities() {

        return this.authorities;
    }


    @JsonIgnore
    public String getEmail() {

        return this.email;
    }


    @JsonProperty
    public void setEmail(String email) {

        this.email = email;
    }


    @JsonIgnore
    public boolean isAdmin() {

        return authorities.contains(new SLAuthority(SLAuthority.ADMIN));
    }


    @JsonDeserialize(contentAs = SLAuthority.class)
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {

        this.authorities.clear();

        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
    }


    @Override
    @JsonIgnore
    public String getEntityId() {

        return getUsername();
    }


    @Override
    @JsonIgnore
    public void setEntityId(String s) {

        setUsername(s);
    }


    @JsonIgnore
    public void setCreatedAt(Date createdAt) {

        if (createdAt == null) {
            createdAt = new Date();
        }

        this.createdAt = createdAt;
    }
}
