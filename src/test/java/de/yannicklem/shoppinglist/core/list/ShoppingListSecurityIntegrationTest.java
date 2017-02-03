package de.yannicklem.shoppinglist.core.list;

import de.yannicklem.shoppinglist.TestUtils;
import de.yannicklem.shoppinglist.WebShoppingListApplication;
import de.yannicklem.shoppinglist.core.article.entity.Article;
import de.yannicklem.shoppinglist.core.article.persistence.ArticleService;
import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.item.persistence.ItemService;
import de.yannicklem.shoppinglist.core.list.entity.ShoppingList;
import de.yannicklem.shoppinglist.core.list.persistence.ShoppingListService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebShoppingListApplication.class)
public class ShoppingListSecurityIntegrationTest {

    private final String shoppingListsEndpoint = "/shoppingLists";
    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private SLUserService slUserService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ShoppingListService shoppingListService;

    private MockMvc mockMvc;

    private SLUser userOne;
    private SLUser userTwo;
    private SLUser admin;

    private Article articleOfUserOne;
    private Article articleOfUserTwo;

    private Item itemOfUserOne;
    private Item itemOfUserTwo;

    private ShoppingList shoppingListOfUserOne;
    private ShoppingList shoppingListOfUserTwo;

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

        itemOfUserOne = new Item(articleOfUserOne, "2", new HashSet<>());
        itemOfUserOne.getOwners().add(userOne);

        itemOfUserTwo = new Item(articleOfUserTwo, "3", new HashSet<>());
        itemOfUserTwo.getOwners().add(userTwo);

        itemService.deleteAll();
        itemService.create(itemOfUserOne);
        itemService.create(itemOfUserTwo);

        shoppingListOfUserOne = new ShoppingList();
        shoppingListOfUserOne.getOwners().add(userOne);
        shoppingListOfUserOne.getItems().add(itemOfUserOne);

        shoppingListOfUserTwo = new ShoppingList();
        shoppingListOfUserTwo.getOwners().add(userTwo);
        shoppingListOfUserTwo.getItems().add(itemOfUserTwo);
    }


    @Test
    public void getShoppingListsAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(get(shoppingListsEndpoint)).andExpect(status().isUnauthorized());
    }
}
