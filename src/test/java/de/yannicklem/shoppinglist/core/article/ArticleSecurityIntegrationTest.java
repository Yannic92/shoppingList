package de.yannicklem.shoppinglist.core.article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.yannicklem.shoppinglist.TestUtils;
import de.yannicklem.shoppinglist.WebShoppingListApplication;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebShoppingListApplication.class)
@WebAppConfiguration
public class ArticleSecurityIntegrationTest {

    private final String articlesEndpoint = "/articles";
    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private SLUserService slUserService;

    @Autowired
    private ArticleService articleService;

    private MockMvc mockMvc;

    private SLUser userOne;
    private SLUser userTwo;
    private SLUser admin;

    private Article articleOfUserOne;
    private Article articleOfUserTwo;

    @Before
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).addFilter(springSecurityFilterChain).build();

        userOne = TestUtils.completelyInitializedTestUser("TestUser");
        userTwo = TestUtils.completelyInitializedTestUser("AnotherTestUser");
        admin = TestUtils.completelyInitializedTestAdmin("Admin");
        slUserService.deleteAll();
        slUserService.create(userOne);
        slUserService.create(userTwo);
        slUserService.create(admin);

        articleOfUserOne = new Article("UserOneArticle", 9.99, new HashSet<>());
        articleOfUserOne.getOwners().add(userOne);
        articleOfUserTwo = new Article("UserTwoArticle", 9.98, new HashSet<>());
        articleOfUserTwo.getOwners().add(userTwo);

        articleService.deleteAll();
        articleService.create(articleOfUserOne);
        articleService.create(articleOfUserTwo);
    }


    // GET-Tests
    @Test
    public void getArticlesReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(articlesEndpoint)).andExpect(status().isUnauthorized());
    }


    @Test
    public void getArticlesReturnsArticlesForAdmin() throws Exception {

        mockMvc.perform(get(articlesEndpoint).with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.articles", hasSize(2)));
    }


    @Test
    public void getArticlesAsUserOneReturnsOnlyOwnArticles() throws Exception {

        mockMvc.perform(get(articlesEndpoint).with(user(userOne)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.articles", hasSize(1)))
            .andExpect(jsonPath("_embedded.articles[0].name", is(articleOfUserOne.getName())));
    }


    @Test
    public void getArticlesAsUserTwoReturnsOnlyOwnArticles() throws Exception {

        mockMvc.perform(get(articlesEndpoint).with(user(userTwo)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.articles", hasSize(1)))
            .andExpect(jsonPath("_embedded.articles[0].name", is(articleOfUserTwo.getName())));
    }


    @Test
    public void getArticleOfUserOneReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(articlesEndpoint + "/" + articleOfUserOne.getEntityId()))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void getArticleOfUserTwoAsUserOneReturnsForbidden() throws Exception {

        mockMvc.perform(get(articlesEndpoint + "/" + articleOfUserTwo.getEntityId()).with(user(userOne)))
            .andExpect(status().isForbidden());
    }


    @Test
    public void getArticleOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        mockMvc.perform(get(articlesEndpoint + "/" + articleOfUserOne.getEntityId()).with(user(userTwo)))
            .andExpect(status().isForbidden());
    }


    @Test
    public void getArticleOfUserOneAsAdminReturnsArticle() throws Exception {

        mockMvc.perform(get(articlesEndpoint + "/" + articleOfUserOne.getEntityId()).with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("name", is(articleOfUserOne.getName())));
    }


    // DELETE-Tests

    @Test
    public void deleteArticleOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        mockMvc.perform(delete(articlesEndpoint + "/" + articleOfUserOne.getEntityId()).with(csrf())
                .with(user(userTwo)))
            .andExpect(status().isForbidden());

        assertThat(articleService.exists(articleOfUserOne.getEntityId()), is(true));
    }


    @Test
    public void deleteArticleOfUserOneAsAdminReturnsNoContentButArticleRemains() throws Exception {

        mockMvc.perform(delete(articlesEndpoint + "/" + articleOfUserOne.getEntityId()).with(csrf()).with(user(admin)))
            .andExpect(status().isNoContent());

        // Not deleted because article still has owners
        assertThat(articleService.exists(articleOfUserOne.getEntityId()), is(true));
    }


    @Test
    public void deleteArticleOfUserOneAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(delete(articlesEndpoint + "/" + articleOfUserOne.getEntityId()).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(articleService.exists(articleOfUserOne.getEntityId()), is(true));
    }


    // PUT-Tests

    @Test
    public void createArticleViaPutAsUserOneCreatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(put(articlesEndpoint + "/1337").content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("name", is(newArticle.getName())));
    }


    @Test
    public void createArticleViaPutAsAdminCreatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(put(articlesEndpoint + "/1337").content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(admin)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("name", is(newArticle.getName())));
    }


    @Test
    public void createArticleViaPutAsAnonymousUserReturnsUnauthorized() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(put(articlesEndpoint + "/1337").content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateArticleNameOfUserOneAsUserOneUpdatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article(articleOfUserOne.getName(), articleOfUserOne.getPriceInEuro(),
                articleOfUserOne.getOwners());
        newArticle.setEntityId(articleOfUserOne.getEntityId());
        newArticle.setName("AnotherName");

        mockMvc.perform(put(articlesEndpoint + "/" + newArticle.getEntityId()).content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is(newArticle.getName())))
            .andExpect(jsonPath("name", is(not(articleOfUserOne.getName()))));
    }


    @Test
    public void updateArticleOwnersOfUserOneAsUserOneHasNoEffectAndReturnsArticle() throws Exception {

        Article newArticle = new Article(articleOfUserOne.getName(), articleOfUserOne.getPriceInEuro(),
                articleOfUserOne.getOwners());
        newArticle.setEntityId(articleOfUserOne.getEntityId());
        newArticle.getOwners().remove(userOne);
        newArticle.getOwners().add(userTwo);

        mockMvc.perform(put(articlesEndpoint + "/" + newArticle.getEntityId()).content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is(newArticle.getName())));

        Set<SLUser> updatedOwners = articleService.findById(newArticle.getEntityId()).getOwners();

        assertThat(updatedOwners, hasSize(1));
        assertThat(updatedOwners.iterator().next(), is(userOne));
    }


    @Test
    public void updateArticleNameOfUserOneAsAdminUpdatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article(articleOfUserOne.getName(), articleOfUserOne.getPriceInEuro(),
                articleOfUserOne.getOwners());
        newArticle.setEntityId(articleOfUserOne.getEntityId());
        newArticle.setName("AnotherName");

        mockMvc.perform(put(articlesEndpoint + "/" + newArticle.getEntityId()).content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is(newArticle.getName())))
            .andExpect(jsonPath("name", is(not(articleOfUserOne.getName()))));
    }


    @Test
    public void updateArticleNameOfUserOneAsAnonymousUserReturnsUnauthorized() throws Exception {

        Article newArticle = new Article(articleOfUserOne.getName(), articleOfUserOne.getPriceInEuro(),
                articleOfUserOne.getOwners());
        newArticle.setEntityId(articleOfUserOne.getEntityId());
        newArticle.setName("AnotherName");

        mockMvc.perform(put(articlesEndpoint + "/" + newArticle.getEntityId()).content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateArticleNameOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        Article newArticle = new Article(articleOfUserOne.getName(), articleOfUserOne.getPriceInEuro(),
                articleOfUserOne.getOwners());
        newArticle.setEntityId(articleOfUserOne.getEntityId());
        newArticle.setName("AnotherName");

        mockMvc.perform(put(articlesEndpoint + "/" + newArticle.getEntityId()).content(getJsonBytes(newArticle))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userTwo)))
            .andExpect(status().isForbidden());
    }


    // POST-Tests

    @Test
    public void createArticleViaPostAsUserOneCreatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(post(articlesEndpoint).content(getJsonBytes(newArticle)).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(userOne)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("name", is(newArticle.getName())));
    }


    @Test
    public void createArticleViaPostAsAdminCreatesAndReturnsArticle() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(post(articlesEndpoint).content(getJsonBytes(newArticle)).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()).with(user(admin)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("name", is(newArticle.getName())));
    }


    @Test
    public void createArticleViaPostAsAnonymousUserReturnsUnauthorized() throws Exception {

        Article newArticle = new Article("newArticle", 213.32, new HashSet<>());

        mockMvc.perform(post(articlesEndpoint).content(getJsonBytes(newArticle)).contentType(
                        MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isUnauthorized());
    }


    private byte[] getJsonBytes(Article article) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsBytes(article);
    }
}
