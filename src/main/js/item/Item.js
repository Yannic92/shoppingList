import Article from '../article/Article';

export default class Item {

    constructor({links, article = new Article(), entityId, count, done = false, lastModified = Date.now()} = {}) {

        this.key = 'entityId';
        this.links = links;
        this.entityId = entityId;
        this.article = this.getArticle(article);
        this.count = count;
        this.done = done;
        this.lastModified = lastModified;
    }

    getArticle(article) {
        if(article instanceof Article) {
            return article;
        } else {
            return new Article(article);
        }
    }
}
