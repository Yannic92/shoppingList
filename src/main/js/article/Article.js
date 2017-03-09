export default class Article {

    constructor({ name, priceInEuro = 0, entityId, links = [], lastModified =  Date.now()} = {} ) {

        this.key = 'entityId';
        this._links = links;
        this.entityId = entityId;
        this.name = name;
        this.priceInEuro = priceInEuro;
        this.lastModified = lastModified;
    }

    equals(article) {
        return article.name === this.name;
    }
}