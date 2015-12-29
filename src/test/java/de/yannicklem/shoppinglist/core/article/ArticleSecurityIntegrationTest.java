package de.yannicklem.shoppinglist.core.article;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.HashSet;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private Article articleOfAdmin;

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
        articleOfAdmin = new Article("AdminArticle", 9.97, new HashSet<>());
        articleOfAdmin.getOwners().add(admin);

        articleService.deleteAll();
        articleService.create(articleOfUserOne);
        articleService.create(articleOfUserTwo);
        articleService.create(articleOfAdmin);
    }


    @Test
    public void getArticlesReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(articlesEndpoint)).andExpect(status().isUnauthorized());
    }


    @Test
    public void getArticlesReturnsArticlesForAdmin() throws Exception {

        mockMvc.perform(get(articlesEndpoint).with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON))
            .andExpect(jsonPath("_embedded.articles", hasSize(3)));
    }
}
