package de.yannicklem.shoppinglist.core.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static de.yannicklem.restutils.entity.SlMediaTypes.HAL_JSON_UTF8;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebShoppingListApplication.class)
public class ItemSecurityIntegrationTest {

    private final String itemsEndpoint = "/api/items";
    private final String listsEndpoint = "/api/shoppingLists";

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

    private ShoppingList listOfUserOne;
    private ShoppingList listOfUserTwo;

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
        articleOfUserOne.setEntityId(UUID.randomUUID().toString());
        articleOfUserTwo = new Article("UserTwoArticle", 9.98, new HashSet<>());
        articleOfUserTwo.getOwners().add(userTwo);
        articleOfUserTwo.setEntityId(UUID.randomUUID().toString());

        articleService.deleteAll();

        itemOfUserOne = new Item(articleOfUserOne, "2 x", new HashSet<>());
        itemOfUserOne.getOwners().add(userOne);
        itemOfUserOne.setEntityId(UUID.randomUUID().toString());

        itemOfUserTwo = new Item(articleOfUserTwo, "2 x", new HashSet<>());
        itemOfUserTwo.getOwners().add(userTwo);
        itemOfUserTwo.setEntityId(UUID.randomUUID().toString());

        itemService.deleteAll();
        itemService.create(itemOfUserOne);
        itemService.create(itemOfUserTwo);

        listOfUserOne = new ShoppingList();
        listOfUserOne.setEntityId(UUID.randomUUID().toString());
        listOfUserOne.setName("userOneList");
        listOfUserOne.setItems(singleton(itemOfUserOne));
        listOfUserOne.setOwners(singleton(userOne));

        listOfUserTwo = new ShoppingList();
        listOfUserTwo.setEntityId(UUID.randomUUID().toString());
        listOfUserTwo.setName("userTwoList");
        listOfUserTwo.setItems(singleton(itemOfUserTwo));
        listOfUserTwo.setOwners(singleton(userTwo));

        shoppingListService.create(listOfUserOne);
        shoppingListService.create(listOfUserTwo);
    }


    // GET-Tests
    @Test
    public void getItemsReturnsUnauthorizedForAnonymousUsers() throws Exception {

        mockMvc.perform(get(itemsEndpoint)).andExpect(status().isUnauthorized());
    }


    @Test
    public void getItemsAsAdminReturnsItems() throws Exception {

        mockMvc.perform(get(itemsEndpoint).with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded.items", hasSize(2)));
    }


    @Test
    public void getItemsAsUserOneReturnsOnlyItemsOfUserOne() throws Exception {

        mockMvc.perform(get(itemsEndpoint).with(user(userOne)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded.items", hasSize(1)))
            .andExpect(jsonPath("_embedded.items[0].count", is(itemOfUserOne.getCount())));
    }


    @Test
    public void getItemsAsUserTwoReturnsOnlyItemsOfUserTwo() throws Exception {

        mockMvc.perform(get(itemsEndpoint).with(user(userTwo)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("_embedded.items", hasSize(1)))
            .andExpect(jsonPath("_embedded.items[0].count", is(itemOfUserTwo.getCount())));
    }


    @Test
    public void getItemOfUserOneAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(get(itemsEndpoint + "/" + itemOfUserOne.getEntityId())).andExpect(status().isUnauthorized());
    }


    @Test
    public void getItemOfListOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        mockMvc.perform(get(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items").with(user(userTwo)))
            .andExpect(status().isForbidden());
    }


    @Test
    public void getItemsOfListOfUserOneAsUserOneReturnsItem() throws Exception {

        mockMvc.perform(get(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items").with(user(userOne)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("_embedded.items", hasSize(1)))
            .andExpect(jsonPath("_embedded.items[0].count", is(itemOfUserOne.getCount())));
    }


    @Test
    public void getItemsOfListOfUserOneAsAdminReturnsItem() throws Exception {

        mockMvc.perform(get(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items").with(user(admin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(HAL_JSON_UTF8))
            .andExpect(jsonPath("_embedded.items", hasSize(1)))
            .andExpect(jsonPath("_embedded.items[0].count", is(itemOfUserOne.getCount())));
    }


    // DELETE-Test

    @Test
    public void deleteItemOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        mockMvc.perform(delete(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + itemOfUserOne.getEntityId()).with(csrf()).with(user(userTwo)))
            .andExpect(status().isForbidden());

        assertThat(itemService.exists(itemOfUserOne.getEntityId()), is(true));
    }


    @Test
    public void deleteItemOfUserOneAsAnonymousUserReturnsUnauthorized() throws Exception {

        mockMvc.perform(delete(itemsEndpoint + "/" + itemOfUserOne.getEntityId()).with(csrf()))
            .andExpect(status().isUnauthorized());

        assertThat(itemService.exists(itemOfUserOne.getEntityId()), is(true));
    }


    @Test
    public void deleteItemOfUserOneAsUserOneReturnsNoContent() throws Exception {

        mockMvc.perform(delete(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + itemOfUserOne.getEntityId()).with(csrf()).with(user(userOne)))
            .andExpect(status().isNoContent());

        assertThat(itemService.exists(itemOfUserOne.getEntityId()), is(false));
        assertThat(articleService.exists(itemOfUserOne.getArticle().getEntityId()), is(true));
    }


    @Test
    public void deleteItemOfUserOneAsAdminReturnsNoContent() throws Exception {

        mockMvc.perform(delete(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + itemOfUserOne.getEntityId()).with(csrf()).with(user(admin)))
            .andExpect(status().isNoContent());

        assertThat(itemService.exists(itemOfUserOne.getEntityId()), is(false));
        assertThat(articleService.exists(itemOfUserOne.getArticle().getEntityId()), is(true));
    }


    // PUT-Tests

    @Test
    public void createItemWithArticleOfUserOneViaPutAsUserOneCreatesAndReturnsItem() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23 x", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());
        newItem.getOwners().add(userOne);

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/1337").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isCreated());

        assertThat(itemService.exists("1337"), is(true));
        assertThat(itemService.findById("1337").get().getArticle().getOwners(), is(newItem.getOwners()));
    }


    @Test
    public void createItemWithArticleOfUserOneViaPutAsUserTwoReturnsForbidden() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23 x", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserTwo.getEntityId() + "/items/1337").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userTwo)))
            .andExpect(status().isForbidden());
    }


    @Test
    public void createItemWithArticleOfUserOneViaPutAsAdminCreatesAndReturnsItem() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23 x", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());
        newItem.setOwners(singleton(userOne));

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/1337").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(admin)))
            .andExpect(status().isCreated());

        HashSet<SLUser> newOwners = new HashSet<>();
        newOwners.addAll(newItem.getOwners());

        assertThat(itemService.exists("1337"), is(true));
        assertThat(itemService.findById("1337").get().getArticle().getOwners(), is(newOwners));
    }


    @Test
    public void updateItemCountOfUserOneAsUserOneUpdates() throws Exception {

        Item updatedItem = new Item(itemOfUserOne);
        updatedItem.setEntityId(itemOfUserOne.getEntityId());
        updatedItem.setCount(itemOfUserOne.getCount() + "1");

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + updatedItem.getEntityId()).content(getJsonBytes(updatedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isOk());
    }


    @Test
    public void updateItemOwnersOfUserOneAsUserOneHasNoEffect() throws Exception {

        Item updatedItem = new Item(itemOfUserOne);
        updatedItem.setEntityId(itemOfUserOne.getEntityId());
        updatedItem.getOwners().remove(userOne);
        updatedItem.getOwners().add(userTwo);

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + updatedItem.getEntityId()).content(getJsonBytes(updatedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
            .andExpect(status().isOk());

        Set<SLUser> updatedOwners = itemService.findById(updatedItem.getEntityId()).get().getOwners();

        assertThat(updatedOwners, hasSize(1));
        assertThat(updatedOwners.iterator().next(), is(userOne));
    }


    @Test
    public void updateItemCountOfUserOneAsAdminUpdatesAndReturnsItem() throws Exception {

        Item updatedItem = new Item(itemOfUserOne);
        updatedItem.setEntityId(itemOfUserOne.getEntityId());
        updatedItem.setCount(itemOfUserOne.getCount() + "1");

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + updatedItem.getEntityId()).content(getJsonBytes(updatedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(admin)))
            .andExpect(status().isOk());
    }


    @Test
    public void updateItemCountOfUserOneAsAnonymousUserReturnsUnauthorized() throws Exception {

        Item updatedItem = new Item(itemOfUserOne);
        updatedItem.setEntityId(itemOfUserOne.getEntityId());
        updatedItem.setCount(itemOfUserOne.getCount() + "1");

        mockMvc.perform(put(itemsEndpoint + "/" + updatedItem.getEntityId()).content(getJsonBytes(updatedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateItemCountOfUserOneAsUserTwoReturnsForbidden() throws Exception {

        Item updatedItem = new Item(itemOfUserOne);
        updatedItem.setEntityId(itemOfUserOne.getEntityId());
        updatedItem.setCount(itemOfUserOne.getCount() + "1");

        mockMvc.perform(put(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items/" + updatedItem.getEntityId()).content(getJsonBytes(updatedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userTwo)))
            .andExpect(status().isForbidden());
    }


    // POST-Tests

    @Test
    public void createItemViaPostAsUserOneCreatesAndReturnsItem() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());

        mockMvc.perform(post(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createItemInListOfUserTwoViaPostAsUserOneReturnsForbidden() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());

        mockMvc.perform(post(listsEndpoint + "/" + listOfUserTwo.getEntityId() + "/items").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(userOne)))
                .andExpect(status().isForbidden());
    }


    @Test
    public void createArticleViaPostAsAdminCreates() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());

        mockMvc.perform(post(listsEndpoint + "/" + listOfUserOne.getEntityId() + "/items").content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user(admin)))
                .andDo(print())
            .andExpect(status().isCreated());
    }


    @Test
    public void createArticleViaPostAsAnonymousUserReturnsUnauthorized() throws Exception {

        Item newItem = new Item(articleOfUserOne, "23", new HashSet<>());
        newItem.setEntityId(UUID.randomUUID().toString());

        mockMvc.perform(post(itemsEndpoint).content(getJsonBytes(newItem))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }


    private byte[] getJsonBytes(Item item) throws JsonProcessingException {

        return new ObjectMapper().writeValueAsBytes(item);
    }
}
