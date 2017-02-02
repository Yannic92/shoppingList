export default class Article {

    constructor({ name, priceInEuro = 0, entityId, links = [] } = {} ) {

        this._links = links;
        this.entityId = entityId;
        this.name = name;
        this.priceInEuro = priceInEuro;
    }

    equals(article) {
        return article.name === this.name;
    }
}