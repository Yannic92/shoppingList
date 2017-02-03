package de.yannicklem.shoppinglist.core.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.yannicklem.shoppinglist.SLUserTestEntity;
import de.yannicklem.shoppinglist.TestUtils;
import de.yannicklem.shoppinglist.WebShoppingListApplication;
import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static de.yannicklem.restutils.entity.SlMediaTypes.HAL_JSON_UTF8;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebShoppingListApplication.class)
public class SLUserSecurityIntegrationTest {

    private final String sLUsersEndpoint = "/sLUsers";
    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private SLUserService slUserService;

    private MockMvc mockMvc;

    private SLUser slUserTest;
    private SLUser slUserTest2;
    private SLUser slUserAdmin;

    @Before
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).addFilter(springSecurityFilterChain).build();

        slUserService.deleteAll();
        slUserTest = TestUtils.completelyInitializedTestUser("Test");
        slUserTest2 = TestUtils.completelyInitializedTestUser("Test2");
        slUserAdmin = TestUtils.completelyInitializedTestAdmin("AdminTest");
        slUserService.create(slUserTest);
        slUserService.create(slUserTest2);
        slUserService.create(slUserAdmin);
    }


    @Test
    public void getUserAsUserReturnsUserInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint + "/" + slUserTest.getEntityId()).with(user((slUserTest))))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("createdAt").doesNotExist());
    }


    @Test
    public void getUsersReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint)).andExpect(status().isUnauthorized());
    }


    @Test
    public void getUsersAsUserReturnsAllUsersExceptAdmin() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint).with(user(slUserTest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("_embedded.sLUsers", hasSize(2)));
    }


    @Test
    public void getUsersAsAdminReturnsAllUserInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint).with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("_embedded.sLUsers", hasSize(slUserService.findAll().size())));
    }


    @Test
    public void getAnotherUsersInformationAsUserReturnsTheInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint + "/" + slUserTest.getUsername()).with(user(slUserTest2)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("firstName", is(slUserTest.getFirstName())))
            .andExpect(jsonPath("lastName", is(slUserTest.getLastName())))
            .andExpect(jsonPath("email").doesNotExist())
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());
    }


    @Test
    public void getAnotherUsersInformationAsAdminReturnsTheInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint + "/" + slUserTest.getUsername()).with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("firstName", is(slUserTest.getFirstName())))
            .andExpect(jsonPath("lastName", is(slUserTest.getLastName())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationAndWithoutCSRFViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationAndWithoutCSRFViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserAsUserViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserAsUserViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAUserWithoutAuthenticationViaPostCreatesAUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserWithoutAuthenticationViaPutCreatesAUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsUserCreatesViaPostTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email").doesNotExist())
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsUserCreatesViaPutTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email").doesNotExist())
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsAdminViaPostCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());
        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsAdminViaPutCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAnAdminUserAsAdminViaPostCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAnAdminUserAsAdminViaPutCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = getJsonBytes(new SLUserTestEntity(sLUserTest3));
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("firstName", is(sLUserTest3.getFirstName())))
            .andExpect(jsonPath("lastName", is(sLUserTest3.getLastName())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail().toLowerCase())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserThatAlreadyExistsAsUserRetrunsBadReqeuest() throws Exception {

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserTest2));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserTest2.getUsername()), is(true));
    }


    @Test
    public void createAUserThatAlreadyExistsAsAdminReturnsBadRequest() throws Exception {

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserTest2));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserTest2.getUsername()), is(true));
    }


    @Test
    public void createAnAdminThatAlreadyExistsAsUserReturnsForbidden() throws Exception {

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserAdmin));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(slUserAdmin.getUsername()), is(true));
    }


    @Test
    public void createAndInvalidUserViaPostAsAnonymousUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsAnonymousUserReturnsUnauthorized() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPostAsUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPostAsAdminUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsAdminUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserInvalid));
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(slUserService.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void updateUserAsAnonymousUserReturnsForbidden() throws Exception {

        SLUserTestEntity slUserTestEntity = new SLUserTestEntity(slUserTest);
        slUserTestEntity.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = getJsonBytes(slUserTestEntity);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(slUserService.findById(slUserTest.getUsername()).get().getEmail(), is(not("changed@hska.de")));
    }


    @Test
    public void updateUserAsSameUserToAdminHasNoEffect() throws Exception {

        SLUserTestEntity slUserTestEntity = new SLUserTestEntity(slUserTest);
        slUserTestEntity.getAuthorities().add(new SLAuthority(SLAuthority.ADMIN));

        byte[] sLUserTest2JsonBytes = getJsonBytes(slUserTestEntity);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isOk());

        assertThat(slUserService.findById(slUserTest.getUsername()).get().isAdmin(), is(false));
    }


    @Test
    public void updateUserAsAnotherUserReturnsForbidden() throws Exception {

        SLUserTestEntity slUserTestEntity = new SLUserTestEntity(slUserTest);
        slUserTestEntity.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = getJsonBytes(slUserTestEntity);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest2)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.findById(slUserTest.getUsername()).get().getEmail(), is(not("changed@hska.de")));
    }


    @Test
    public void updateUserAsSameUserUpdatesUser() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserTest));
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("firstName", is(slUserTest.getFirstName())))
            .andExpect(jsonPath("lastName", is(slUserTest.getLastName())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.findById(slUserTest.getUsername()).get().getEmail(), is("changed@hska.de"));
    }


    @Test
    public void updateUserAsAdminUpdatesUser() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserTest));
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("firstName", is(slUserTest.getFirstName())))
            .andExpect(jsonPath("lastName", is(slUserTest.getLastName())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities").doesNotExist())
            .andExpect(jsonPath("password").doesNotExist());

        assertThat(slUserService.findById(slUserTest.getUsername()).get().getEmail(), is("changed@hska.de"));
    }


    @Test
    public void deleteUserAsAnonymousUserReturnsUnauthorized() throws Exception {

        byte[] sLUserTest2JsonBytes = getJsonBytes(new SLUserTestEntity(slUserTest));
        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(slUserService.exists(slUserTest.getUsername()), is(true));
    }


    @Test
    public void deleteUserAsAnotherUserReturnsForbidden() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(slUserTest2)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists(slUserTest.getUsername()), is(true));
    }


    @Test
    public void deleteUserAsAdminDeletesUser() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(slUserAdmin)))
            .andExpect(status().isNoContent());

        assertThat(slUserService.exists(slUserTest.getUsername()), is(false));
    }


    @Test
    public void deleteNonExistingUserAsAdminReturnsNotFound() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isNotFound());

        assertThat(slUserService.exists("nonExistent"), is(false));
    }


    @Test
    public void deleteNonExistingUserAsUserReturnsForbidden() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(slUserService.exists("nonExistent"), is(false));
    }


    @Test
    public void deleteNonExistingUserAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(slUserService.exists("nonExistent"), is(false));
    }


    private byte[] getJsonBytes(SLUserTestEntity slUser) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsBytes(slUser);
    }
}
