import Endpoints from './Endpoints';

class EntityType {

    constructor(type) {
        this.value = type;
    }
}

const EntityTypes = {
    LISTS: new EntityType('lists'),
    LIST: new EntityType('list'),
    ITEMS: new EntityType('items'),
    ITEM: new EntityType('item'),
    ARTICLES: new EntityType('articles'),
    ARTICLE: new EntityType('article'),
    USERS: new EntityType('users'),
    USER: new EntityType('user'),
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
