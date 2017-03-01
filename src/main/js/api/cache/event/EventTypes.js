export default class EventTypes {

    constructor(type) {
        this.value = type;
    }

    equals(otherEventType) {

        if(otherEventType.value) {
            return otherEventType.value == this.value;
        }

        return false;
    }
}

EventTypes.LISTS_UPDATED = new EventTypes('lists-updated');
EventTypes.LIST_UPDATED = new EventTypes('list-updated');
EventTypes.ITEMS_UPDATED = new EventTypes('items-updated');
EventTypes.ITEM_UPDATED = new EventTypes('item-updated');
EventTypes.ARTICLES_UPDATED = new EventTypes('articles-updated');
EventTypes.ARTICLE_UPDATED = new EventTypes('article-updated');
EventTypes.USERS_UPDATED = new EventTypes('users-updated');
EventTypes.USER_UPDATED = new EventTypes('user-updated');

EventTypes.CACHE_OUTDATED = new EventTypes('cache-outdated');