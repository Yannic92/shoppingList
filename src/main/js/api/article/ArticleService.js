import AttributeEqualsFilter from '../AttributeEqualsFilter';

export default class ArticleService {

    /*@ngInject*/
    constructor(articleRestService, $q) {

        this.restService = articleRestService;
        this.articles = this.restService.container;
        this.Promise = $q;
    }

    getAllArticles(refetch = false) {
        if (refetch || this.articlesAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.articles;
    }

    articlesAlreadyFetched() {
        return !this.articles.fetching && !this.articles.fetched;
    }

    findArticleByName(articleName) {
        return AttributeEqualsFilter.findFirstByMatchingAttribute(this.articles, 'name', articleName).element;
    }

    createArticle(article) {

        try {
            const persistedArticleWithSameName = this.findArticleByName(article.name);
            article.entityId = persistedArticleWithSameName.entityId;
            article._links = persistedArticleWithSameName._links;
            return this.Promise.resolve(article);
        } catch (notFoundError) {
            return this.restService.create(article);
        }
    }

    deleteArticle(article) {

        return this.restService.delete(article);
    }

    deleteUnusedArticles() {

        return this.restService.deleteAll();
    }
}