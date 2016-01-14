package de.yannicklem.shoppinglist.core;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.persistence.ItemService;
import de.yannicklem.shoppinglist.core.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import lombok.RequiredArgsConstructor;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public class CleanUpService {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());
    private static final long ONE_HOUR = 1000 * 60 * 60;

    private final SLUserService userService;
    private final ItemService itemService;

    @Scheduled(fixedRate = ONE_HOUR)
    public void cleanUp() {

        LOGGER.info("Cleaning up database..");

        clearNotEnabledUsersOlderThanTwoDays();

        deleteUnusedItems();
    }


    public void clearNotEnabledUsersOlderThanTwoDays() {

        Date twoDaysBefore = new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(2));
        List<SLUser> inactiveUsersOlderThanTwoDays = userService.findInactiveUsersOlderThan(twoDaysBefore);

        if (inactiveUsersOlderThanTwoDays != null && !inactiveUsersOlderThanTwoDays.isEmpty()) {
            LOGGER.info("Deleting inactive users");

            for (SLUser user : inactiveUsersOlderThanTwoDays) {
                userService.delete(user);
            }
        }
    }


    public void deleteUnusedItems() {

        Date oneMinuteAgo = new Date(new Date().getTime() - TimeUnit.MINUTES.toMillis(1));

        List<Item> unsedItems = itemService.findUnusedItems(oneMinuteAgo);

        if (unsedItems != null && !unsedItems.isEmpty()) {
            LOGGER.info("Deleting unused items");

            for (Item unusedItem : unsedItems) {
                itemService.delete(unusedItem);
            }
        }
    }
}
