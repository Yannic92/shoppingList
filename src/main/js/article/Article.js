export default class Article {

    constructor(name, priceInEuro, entityId = undefined, links = []) {

        this._links = links;
        this.entityId = entityId;
        this.name = name;
        this.priceInEuro = priceInEuro;
    }

    static ofResource({name, priceInEuro, entityId, _links}) {

        return new Article(name, priceInEuro, entityId, _links);
    }
    toResource() {

        const resource = {};

        resource.entityId = this.entityId;
        resource.name = this.name;
        resource.priceInEuro = this.priceInEuro;

        return resource;
    }

    equals(article) {
        return article.name === this.name;
    }
}