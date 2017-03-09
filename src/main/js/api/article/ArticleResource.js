export default class ArticleResource {

    constructor(article) {
        this.entityId = article.entityId;
        this.name = article.name;
        this.priceInEuro = article.priceInEuro;
        this.lastModified = article.lastModified;
    }
}
