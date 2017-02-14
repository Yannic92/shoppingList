import EntityTypes from '../EntityTypes';
import EventTypes from './event/EventTypes';
import HALResource from '../HALResource';
import EntityUpdatedEvent from './event/EntityUpdatedEvent';
import UserResourceConverter from '../user/UserResourceConverter';
import ArticleResourceConverter from '../article/ArticleResourceConverter';
import ItemResourceConverter from '../item/ItemResourceConverter';
import ShoppingListResourceConverter from '../list/ShoppingListResourceConverter';

/* global BroadcastChannel */

export default class UpdateNotificationService {

    constructor() {
        this.userResourceConverter = new UserResourceConverter();
        this.articleResourceConverter = new ArticleResourceConverter();
        this.itemResourceConverter = new ItemResourceConverter(this.articleResourceConverter);
        this.shoppingListResourceConverter = new ShoppingListResourceConverter(this.itemResourceConverter);
    }

    sendCacheUpdateNotification(entityType, newBody) {
        switch (entityType.value) {
        case EntityTypes.LISTS.value:
            this._sendListsUpdatedEvent(JSON.parse(newBody));
            break;
        case EntityTypes.LIST.value:
            this._sendListUpdatedEvent(JSON.parse(newBody));
            break;
        case EntityTypes.ITEMS.value:
            this._sendItemsUpdatedEvent(JSON.parse(newBody));
            break;
        case EntityTypes.ITEM.value:
            this._sendItemUpdateEvent(JSON.parse(newBody));
            break;
        case EntityTypes.ARTICLES.value:
            this._sendArticlesUpdateEvent(JSON.parse(newBody));
            break;
        case EntityTypes.ARTICLE.value:
            this._sendArticleUpdateEvent(JSON.parse(newBody));
            break;
        case EntityTypes.USERS.value:
            this._sendUsersUpdateEvent(JSON.parse(newBody));
            break;
        case EntityTypes.USER.value:
            this._sendUserUpdateEvent(JSON.parse(newBody));
            break;
        default: throw 'Unknown EntityType: ' + entityType;
        }
    }

    _sendListsUpdatedEvent(jsonLists) {
        const lists = this.shoppingListResourceConverter.toEntities(HALResource.getContent(jsonLists));
        new BroadcastChannel('list-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.LISTS_UPDATED, lists));
    }

    _sendListUpdatedEvent(jsonList) {
        const list = this.shoppingListResourceConverter.toEntity(jsonList);
        new BroadcastChannel('list-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.LIST_UPDATED, list));
    }

    _sendItemsUpdatedEvent(jsonItems) {
        const items = this.itemResourceConverter.toEntities(HALResource.getContent(jsonItems));
        new BroadcastChannel('item-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.ITEMS_UPDATED, items));
    }

    _sendItemUpdateEvent(jsonItem) {
        const item = this.itemResourceConverter.toEntity(jsonItem);
        new BroadcastChannel('item-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.ITEM_UPDATED, item));
    }

    _sendArticlesUpdateEvent(jsonArticles) {
        const articles = this.articleResourceConverter.toEntities(HALResource.getContent(jsonArticles));
        new BroadcastChannel('article-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.ARTICLES_UPDATED, articles));
    }

    _sendArticleUpdateEvent(jsonArticle) {
        const article = this.articleResourceConverter.toEntity(jsonArticle);
        new BroadcastChannel('article-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.ARTICLE_UPDATED, article));
    }

    _sendUsersUpdateEvent(jsonUsers) {
        const users = this.userResourceConverter.toEntities(HALResource.getContent(jsonUsers));
        new BroadcastChannel('user-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.USERS_UPDATED, users));
    }

    _sendUserUpdateEvent(jsonUser) {
        const user = this.userResourceConverter.toEntity(jsonUser);
        new BroadcastChannel('user-cache-updated').postMessage(new EntityUpdatedEvent(EventTypes.USER_UPDATED, user));
    }
}