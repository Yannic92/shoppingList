import Article from '../article/Article';

export default class Item {

    constructor({links, article = new Article(), entityId, count, done = false} = {}) {

        this.links = links;
        this.entityId = entityId;
        this.article = article;
        this.count = count;
        this.done = done;
    }
}
