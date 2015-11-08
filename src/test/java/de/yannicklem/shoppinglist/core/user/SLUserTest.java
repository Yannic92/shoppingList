package de.yannicklem.shoppinglist.core.user;

import de.yannicklem.shoppinglist.TestUtils;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;

import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.collection.IsEmptyCollection.empty;

import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class SLUserTest {

    private SLUser sut;

    @Before
    public void setup() {

        sut = new SLUser();
    }


    @Test
    public void userWithAdminAuthorityIsAdmin() {

        sut.getAuthorities().add(new SLAuthority(SLAuthority.USER));
        sut.getAuthorities().add(new SLAuthority(SLAuthority.ADMIN));

        assertThat(sut.isAdmin(), is(true));
    }


    @Test
    public void userWithOutAdminAuthorityIsNoAdmin() {

        sut.getAuthorities().add(new SLAuthority(SLAuthority.USER));

        assertThat(sut.isAdmin(), is(false));
    }


    @Test
    public void getUsernameReturnsPreviouslySetUsername() {

        String username = "Test";
        sut.setUsername(username);

        assertThat(sut.getUsername(), is(username));
    }


    @Test
    public void getPasswordReturnsPreviouslySetPassword() {

        String password = "secret";
        sut.setPassword(password);

        assertThat(sut.getPassword(), is(password));
    }


    @Test
    public void getAuthoritiesReturnsPreviouslySetAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SLAuthority(SLAuthority.ADMIN));

        sut.setAuthorities(authorities);

        assertThat(sut.getAuthorities(), is(authorities));
    }


    @Test
    public void getAuthoritiesReturnsEmptySetIfPreviouslySetToNull() {

        sut.setAuthorities(null);

        assertThat(sut.getAuthorities(), is(empty()));
    }


    @Test
    public void isEnabledReturnsPreviouslySetEnabled() {

        boolean enabled = !sut.isEnabled();

        sut.setEnabled(enabled);

        assertThat(sut.isEnabled(), is(enabled));
    }


    @Test
    public void isAccountNonExpiredReturnsPreviouslySetAccountNonExpired() {

        boolean accountNonExpired = !sut.isAccountNonExpired();

        sut.setAccountNonExpired(accountNonExpired);

        assertThat(sut.isAccountNonExpired(), is(accountNonExpired));
    }


    @Test
    public void isAccountNonLockedReturnsPreviouslySetAccountNonLocked() {

        boolean accountNonLocked = !sut.isAccountNonLocked();

        sut.setAccountNonLocked(accountNonLocked);

        assertThat(sut.isAccountNonLocked(), is(accountNonLocked));
    }


    @Test
    public void isCredentialsNonExpiredReturnsPreviouslySetCredentialsNonExpired() {

        boolean credentialsNonExpired = !sut.isCredentialsNonExpired();

        sut.setCredentialsNonExpired(credentialsNonExpired);

        assertThat(sut.isCredentialsNonExpired(), is(credentialsNonExpired));
    }


    @Test
    public void getEmailReturnsPreviouslySetEmail() {

        String email = "test@test.de";

        sut.setEmail(email);

        assertThat(sut.getEmail(), is(email));
    }


    @Test
    public void usersWithSameUsernameAreEqual() {

        sut.setUsername("test");

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentAuthoritiesAreEqual() {

        sut.setUsername("test");
        sut.getAuthorities().add(new SLAuthority(SLAuthority.ADMIN));

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.getAuthorities().add(new SLAuthority(SLAuthority.USER));

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentEmailAreEqual() {

        sut.setUsername("test");
        sut.setEmail("test@test.de");

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.setEmail(sut.getEmail() + "1");

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentPasswordAreEqual() {

        sut.setUsername("test");
        sut.setPassword("Troll");

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.setPassword(sut.getPassword() + "1");

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentAccountNonExpiredAreEqual() {

        sut.setUsername("test");
        sut.setAccountNonExpired(true);

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.setAccountNonExpired(false);

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentAccountNonLockedAreEqual() {

        sut.setUsername("test");
        sut.setAccountNonLocked(true);

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.setAccountNonLocked(false);

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void usersWithSameUsernameAndDifferentCredentialsNonExpiredAreEqual() {

        sut.setUsername("test");
        sut.setCredentialsNonExpired(true);

        SLUser otherUser = new SLUser();

        otherUser.setUsername(sut.getUsername());
        otherUser.setCredentialsNonExpired(false);

        assertThat(sut.equals(otherUser), is(true));
    }


    @Test
    public void argsConstructorSetsPassedArguments() {

        String username = "Test";
        String firstname = "Test";
        String lastName = "Müller";
        String email = "Test@test.de";
        String password = "secrect";
        Confirmation confirmation = TestUtils.completelyInitializedConfirmation();
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SLAuthority(SLAuthority.ADMIN));
        sut = new SLUser(username, firstname, lastName, password, email, false, confirmation, authorities);

        assertThat(sut.getUsername(), is(username));
        assertThat(sut.getEmail(), is(email));
        assertThat(sut.getPassword(), is(password));
        assertThat(sut.getAuthorities(), is(authorities));
        assertThat(sut.isEnabled(), is(false));
        assertThat(sut.getFirstName(), is(firstname));
        assertThat(sut.getLastName(), is(lastName));
        assertThat(sut.getConfirmation(), is(confirmation));
    }


    @Test
    public void toStringDoesNotContainPassword() {

        sut.setPassword("secret");

        assertThat(sut.toString().contains("password"), is(false));
        assertThat(sut.toString().contains("secret"), is(false));
    }
}
