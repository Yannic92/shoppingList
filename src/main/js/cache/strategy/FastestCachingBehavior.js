import CachingBehavior from '../CachingBehavior';
import toolbox from 'sw-toolbox';
import ArticleResourceConverter from '../../api/article/ArticleResourceConverter';
import ShoppingListResourceConverter from '../../api/list/ShoppingListResourceConverter';
import ItemResourceConverter from '../../api/item/ItemResourceConverter';
import UserResourceConverter from '../../api/user/UserResourceConverter';
import HALResource from '../../services/HALResource';
import EntityUpdatedEvent from '../event/EntityUpdatedEvent';
import EventTypes from '../event/EventTypes';
import Endpoints from '../../api/Endpoints';

/* global BroadcastChannel */

export default class FastestCachingBehavior extends CachingBehavior{

    constructor(cacheName) {
        super(cacheName);
        this.shoppingListResourceConverter = new ShoppingListResourceConverter();
        this.itemResourceConverter = new ItemResourceConverter();
        this.articleResourceConverter = new ArticleResourceConverter();
        this.userResourceConverter = new UserResourceConverter();
    }

    strategy(request) {

        toolbox.fastest(request, {
            cache: {
                name: this.cacheName
            }
        });

        return new Promise((resolve, reject) => {
            let rejected = false;
            const reasons = [];

            const maybeReject = (reason) => {
                reasons.push(reason.toString());
                if (rejected) {
                    reject(new Error('Both cache and network failed: \'' +
                        reasons.join('\', \'') + '\''));
                } else {
                    rejected = true;
                }
            };

            const maybeResolve = (result) => {
                if (result instanceof Response) {
                    resolve(result);
                } else {
                    maybeReject('No result returned');
                }
            };

            const resultContainer = {
                networkResult: null,
                cacheResult: null
            };

            this.fetchAndCache(request.clone())
                .then((result) => {
                    const entityType = FastestCachingBehavior.determineEntityType(request.url);
                    return this._handleSuccessfulNetworkResponse(result.clone(), resultContainer, entityType)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    return maybeReject(error);
                });

            this.cacheOnly(request)
                .then((result) => {
                    if(!result) {
                        return maybeReject('Cache result was undefined');
                    }
                    const entityType = FastestCachingBehavior.determineEntityType(request.url);
                    return this._handleSuccessfulCacheResponse(result.clone(), resultContainer, entityType)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    return maybeReject(error);
                });
        });
    }

    _handleSuccessfulNetworkResponse(response, resultContainer, entityType) {
        return this._getBody(response)
            .then((body) => {
                resultContainer.networkResult = body;
            }).then(() => {
                if(resultContainer.cacheResult) {
                    return this.sendUpdateIfBodiesAreNotTheSame(resultContainer.cacheResult, resultContainer.networkResult, entityType);
                }
            }).then(() => {
                return response;
            });
    }

    _handleSuccessfulCacheResponse(response, resultContainer, entityType) {
        return this._getBody(response)
            .then((body) => {
                resultContainer.cacheResult = body;
            }).then(() => {
                if(resultContainer.networkResult) {
                    return this.sendUpdateIfBodiesAreNotTheSame(resultContainer.cacheResult, resultContainer.networkResult, entityType);
                }
            }).then(() => {
                return response;
            });
    }

    _getBody(response) {
        const textDecoder = new TextDecoder();
        const reader = response.body.getReader();

        return reader.read().then((result) => {
            return textDecoder.decode(result.value, {stream: true});
        });
    }

    sendUpdateIfBodiesAreNotTheSame(oldBody, newBody, entityType) {
        if(oldBody != newBody) {

            switch (entityType.value) {
            case EntityType.LISTS.value:
                this._sendListsUpdatedEvent(JSON.parse(newBody));
                break;
            case EntityType.LIST.value:
                this._sendListUpdatedEvent(JSON.parse(newBody));
                break;
            case EntityType.ITEMS.value:
                this._sendItemsUpdatedEvent(JSON.parse(newBody));
                break;
            case EntityType.ITEM.value:
                this._sendItemUpdateEvent(JSON.parse(newBody));
                break;
            case EntityType.ARTICLES.value:
                this._sendArticlesUpdateEvent(JSON.parse(newBody));
                break;
            case EntityType.ARTICLE.value:
                this._sendArticleUpdateEvent(JSON.parse(newBody));
                break;
            case EntityType.USERS.value:
                this._sendUsersUpdateEvent(JSON.parse(newBody));
                break;
            case EntityType.USER.value:
                this._sendUserUpdateEvent(JSON.parse(newBody));
                break;
            default: throw 'Unknown EntityType: ' + entityType;
            }
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

    static determineEntityType(requestUrl) {
        if(requestUrl.endsWith(Endpoints.list)) {
            return EntityType.LISTS;
        }else if(requestUrl.includes(Endpoints.list)) {
            return EntityType.LIST;
        }else if(requestUrl.endsWith(Endpoints.item)) {
            return EntityType.ITEMS;
        }else if(requestUrl.includes(Endpoints.item)) {
            return EntityType.ITEM;
        }else if(requestUrl.endsWith(Endpoints.article)) {
            return EntityType.ARTICLES;
        }else if(requestUrl.includes(Endpoints.article)) {
            return EntityType.ARTICLE;
        }else if(requestUrl.endsWith(Endpoints.user)) {
            return EntityType.USERS;
        }else if(requestUrl.includes(Endpoints.user)) {
            return EntityType.USER;
        }else {
            throw 'unsupported request: ' + requestUrl;
        }
    }

}

class EntityType {

    constructor(type) {
        this.value = type;
    }
}

EntityType.LISTS = new EntityType('lists');
EntityType.LIST = new EntityType('list');
EntityType.ITEMS = new EntityType('items');
EntityType.ITEM = new EntityType('item');
EntityType.ARTICLES = new EntityType('articles');
EntityType.ARTICLE = new EntityType('article');
EntityType.USERS = new EntityType('users');
EntityType.USER = new EntityType('user');