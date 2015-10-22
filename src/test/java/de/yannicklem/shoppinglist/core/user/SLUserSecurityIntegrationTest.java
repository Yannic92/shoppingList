package de.yannicklem.shoppinglist.core.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.yannicklem.shoppinglist.TestUtils;
import de.yannicklem.shoppinglist.WebShoppingListApplication;

import org.hamcrest.Matchers;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.hateoas.MediaTypes;

import org.springframework.http.MediaType;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.CoreMatchers.not;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebShoppingListApplication.class)
@WebAppConfiguration
public class SLUserSecurityIntegrationTest {

    private final String sLUsersEndpoint = "/api/sLUsers";
    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private SLUserRepository sLUserRepository;

    private MockMvc mockMvc;

    private SLUser slUserTest;
    private SLUser slUserTest2;
    private SLUser slUserAdmin;

    @Before
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).addFilter(springSecurityFilterChain).build();

        slUserTest = TestUtils.completelyInitializedTestUser("Test");
        slUserTest2 = TestUtils.completelyInitializedTestUser("Test2");
        slUserAdmin = TestUtils.completelyInitializedTestAdmin("AdminTest");
        sLUserRepository.deleteAll();
        sLUserRepository.save(slUserTest);
        sLUserRepository.save(slUserTest2);
        sLUserRepository.save(slUserAdmin);
    }


    @Test
    public void getUsersReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint)).andExpect(status().isUnauthorized());
    }


    @Test
    public void getUsersAsUserReturnsOnlyOwnUserInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint).with(user(slUserTest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.sLUsers", hasSize(1)))
            .andExpect(jsonPath("_embedded.sLUsers[0].username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("_embedded.sLUsers[0].email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("_embedded.sLUsers[0].authorities",
                        Matchers.hasSize(slUserTest.getAuthorities().size())))
            .andExpect(jsonPath("_embedded.sLUsers[0].authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("_embedded.sLUsers[0].password", is(nullValue())));
    }


    @Test
    public void getUsersAsAdminReturnsAllUserInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint).with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.sLUsers", hasSize(sLUserRepository.findAll().size())));
    }


    @Test
    public void getAnotherUsersInformationAsUserReturnsForbidden() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint + "/" + slUserTest.getUsername()).with(user(slUserTest2)))
            .andExpect(status().isForbidden());
    }


    @Test
    public void getAnotherUsersInformationAsAdminReturnsTheInformation() throws Exception {

        mockMvc.perform(get(sLUsersEndpoint + "/" + slUserTest.getUsername()).with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(slUserTest.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(nullValue())));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationAndWithoutCSRFViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationAndWithoutCSRFViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserWithoutAuthenticationViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserAsUserViaPostReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAnAdminUserAsUserViaPutReturnsForbidden() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(false));
    }


    @Test
    public void createAUserWithoutAuthenticationViaPostCreatesAUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));
        ;

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserWithoutAuthenticationViaPutCreatesAUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));
        ;

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsUserCreatesViaPostTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsUserCreatesViaPutTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsAdminViaPostCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserAsAdminViaPutCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestUser("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAnAdminUserAsAdminViaPostCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[1].authority", is(SLAuthority.ADMIN)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAnAdminUserAsAdminViaPutCreatesTheNewUserAndReturnsCreatedUser() throws Exception {

        SLUser sLUserTest3 = TestUtils.completelyInitializedTestAdmin("Test3");
        byte[] sLUserTest3JsonBytes = new ObjectMapper().writeValueAsBytes(sLUserTest3);
        mockMvc.perform(put(sLUsersEndpoint + "/" + sLUserTest3.getUsername()).content(sLUserTest3JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(sLUserTest3.getUsername())))
            .andExpect(jsonPath("email", is(sLUserTest3.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(sLUserTest3.getAuthorities().size())))
            .andExpect(jsonPath("authorities[1].authority", is(SLAuthority.ADMIN)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.exists(sLUserTest3.getUsername()), is(true));
    }


    @Test
    public void createAUserThatAlreadyExistsAsUserRetrunsBadReqeuest() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest2);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserTest2.getUsername()), is(true));
    }


    @Test
    public void createAUserThatAlreadyExistsAsAdminReturnsBadRequest() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest2);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserTest2.getUsername()), is(true));
    }


    @Test
    public void createAnAdminThatAlreadyExistsAsUserReturnsForbidden() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserAdmin);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(slUserAdmin.getUsername()), is(true));
    }


    @Test
    public void createAndInvalidUserViaPostAsAnonymousUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsAnonymousUserReturnsUnauthorized() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPostAsUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPostAsAdminUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(post(sLUsersEndpoint).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void createAndInvalidUserViaPutAsAdminUserReturnsBadRequest() throws Exception {

        SLUser slUserInvalid = TestUtils.completelyInitializedTestUser("Invalid");
        slUserInvalid.setEmail(null);

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserInvalid);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserInvalid.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isBadRequest());

        assertThat(sLUserRepository.exists(slUserInvalid.getUsername()), is(false));
    }


    @Test
    public void updateUserAsAnonymousUserReturnsForbidden() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.findOne(slUserTest.getUsername()).getEmail(), is(not("changed@hska.de")));
    }


    @Test
    public void updateUserAsSameUserToAdminReturnsForbidden() throws Exception {

        SLUser slUserTestUpdated = new SLUser(slUserTest.getUsername(), slUserTest.getFirstName(),
                slUserTest.getLastName(), slUserTest.getPassword(), slUserTest.getEmail(), slUserTest.isEnabled(),
                slUserTest.getConfirmation(), slUserTest.getAuthorities());
        slUserTestUpdated.getAuthorities().add(new SLAuthority(SLAuthority.ADMIN));

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTestUpdated);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.findOne(slUserTest.getUsername()).getAuthorities().size(), is(1));
    }


    @Test
    public void updateUserAsAnotherUserReturnsForbidden() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest2)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.findOne(slUserTest.getUsername()).getEmail(), is(not("changed@hska.de")));
    }


    @Test
    public void updateUserAsSameUserUpdatesUser() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(slUserTest.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.findOne(slUserTest.getUsername()).getEmail(), is("changed@hska.de"));
    }


    @Test
    public void updateUserAsAdminUpdatesUser() throws Exception {

        slUserTest.setEmail("changed@hska.de");

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(put(sLUsersEndpoint + "/" + slUserTest.getUsername()).content(sLUserTest2JsonBytes)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("username", is(slUserTest.getUsername())))
            .andExpect(jsonPath("email", is(slUserTest.getEmail())))
            .andExpect(jsonPath("authorities", Matchers.hasSize(slUserTest.getAuthorities().size())))
            .andExpect(jsonPath("authorities[0].authority", is(SLAuthority.USER)))
            .andExpect(jsonPath("password", is(not(nullValue()))));

        assertThat(sLUserRepository.findOne(slUserTest.getUsername()).getEmail(), is("changed@hska.de"));
    }


    @Test
    public void deleteUserAsAnonymousUserReturnsUnauthorized() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(sLUserRepository.exists(slUserTest.getUsername()), is(true));
    }


    @Test
    public void deleteUserAsAnotherUserReturnsForbidden() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(slUserTest2)))
            .andExpect(status().isForbidden());

        assertThat(sLUserRepository.exists(slUserTest.getUsername()), is(true));
    }


    @Test
    public void deleteUserAsAdminDeletesUser() throws Exception {

        byte[] sLUserTest2JsonBytes = new ObjectMapper().writeValueAsBytes(slUserTest);
        mockMvc.perform(delete(sLUsersEndpoint + "/" + slUserTest.getUsername()).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(slUserAdmin)))
            .andExpect(status().isNoContent());

        assertThat(sLUserRepository.exists(slUserTest.getUsername()), is(false));
    }


    @Test
    public void deleteNonExistingUserAsAdminReturnsNotFound() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserAdmin)))
            .andExpect(status().isNotFound());

        assertThat(sLUserRepository.exists("nonExistent"), is(false));
    }


    @Test
    public void deleteNonExistingUserAsUserReturnsNotFound() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(slUserTest)))
            .andExpect(status().isNotFound());

        assertThat(sLUserRepository.exists("nonExistent"), is(false));
    }


    @Test
    public void deleteNonExistingUserAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(delete(sLUsersEndpoint + "/nonExistent").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(sLUserRepository.exists("nonExistent"), is(false));
    }
}
