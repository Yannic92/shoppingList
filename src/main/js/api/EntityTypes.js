import Endpoints from './Endpoints';

class EntityType {

    constructor(type, endpoint, isCollection = false) {
        this.value = type;
        this.endpoint = endpoint;
        this.isCollection = isCollection;
    }
}

const EntityTypes = {
    LISTS: new EntityType('lists', Endpoints.list, true),
    LIST: new EntityType('list', Endpoints.list),
    ITEMS: new EntityType('items', Endpoints.item, true),
    ITEM: new EntityType('item', Endpoints.item),
    ARTICLES: new EntityType('articles', Endpoints.article, true),
    ARTICLE: new EntityType('article', Endpoints.article),
    USERS: new EntityType('users', Endpoints.user, true),
    USER: new EntityType('user', Endpoints.user),
    ofUrl: (url) => {
        if(url.endsWith(Endpoints.list)) {
            return EntityTypes.LISTS;
        }else if(url.includes(Endpoints.list)) {
            return EntityTypes.LIST;
        }else if(url.endsWith(Endpoints.item)) {
            return EntityTypes.ITEMS;
        }else if(url.includes(Endpoints.item)) {
            return EntityTypes.ITEM;
        }else if(url.endsWith(Endpoints.article)) {
            return EntityTypes.ARTICLES;
        }else if(url.includes(Endpoints.article)) {
            return EntityTypes.ARTICLE;
        }else if(url.endsWith(Endpoints.user)) {
            return EntityTypes.USERS;
        }else if(url.includes(Endpoints.user)) {
            return EntityTypes.USER;
        }else {
            throw 'unsupported url: ' + url;
        }
    }
};

export default EntityTypes;
