import Endpoints from './Endpoints';
import ShoppingList from '../lists/ShoppingList';
import Item from '../item/Item';
import Article from '../article/Article';
import User from '../user/User';

class EntityType {

    constructor(type, endpoint, entityConstructor) {
        this.value = type;
        this.endpoint = endpoint;
        this.entityConstructor = entityConstructor;
    }
}

const EntityTypes = {
    LIST: new EntityType('list', Endpoints.list, ShoppingList),
    LIST_ITEM: new EntityType('item', Endpoints.listItem, Item),
    ITEM: new EntityType('item', Endpoints.item, Item),
    ARTICLE: new EntityType('article', Endpoints.article, Article),
    USER: new EntityType('user', Endpoints.user, User),
    ofUrl: (url) => {
        if(url.includes('/api/shoppingLists' && url.includes('/items'))){
            return EntityTypes.LIST_ITEM;
        } else if(url.includes(Endpoints.list)) {
            return EntityTypes.LIST;
        } else if(url.includes(Endpoints.item)) {
            return EntityTypes.ITEM;
        }else if(url.includes(Endpoints.article)) {
            return EntityTypes.ARTICLE;
        }else if(url.includes(Endpoints.user)) {
            return EntityTypes.USER;
        }else {
            throw 'unsupported url: ' + url;
        }
    }
};

export default EntityTypes;
