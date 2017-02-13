import RESTService from '../RESTService';
import Endpoints from '../Endpoints';

export default class ArticleService {

    /*@ngInject*/
    constructor($resource, $filter, $q, $rootScope, articleResourceConverter, $timeout) {

        this.$q = $q;
        this.filter = $filter('filter');

        this.articles = [];

        this.restService = new RESTService($rootScope,$q, $resource, articleResourceConverter, this.articles,
            this.filter, $timeout, 'article-cache-updated', Endpoints.article);
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
        const persistedArticlesWithSameName = this.filter(this.articles, {name: articleName}, true);

        if(persistedArticlesWithSameName) {
            return persistedArticlesWithSameName[0];
        }

        return undefined;
    }

    createArticle(article) {

        let persistedArticleWithSameName = this.findArticleByName(article.name);

        if (persistedArticleWithSameName) {
            article.entityId = persistedArticleWithSameName.entityId;
            article._links = persistedArticleWithSameName._links;
            return this.$q((resolve) => resolve(article));
        }

        return this.restService.create(article);
    }

    deleteArticle(article) {

        return this.restService.delete(article);
    }

    deleteUnusedArticles() {

        return this.restService.deleteAll();
    }
}