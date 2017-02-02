import RESTService from '../../global/RESTService';

export default class ArticleService {

    /*@ngInject*/
    constructor($resource, $filter, $q, $rootScope, articleResourceConverter) {

        this.$q = $q;
        this.filter = $filter('filter');

        const articlesResource = $resource('/articles/:entityId', null, {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });

        this.articles = [];

        this.restService = new RESTService($rootScope, $q, articlesResource, articleResourceConverter, this.articles, this.filter);
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

        return this.restService.delete({entityId: article.entityId});
    }

    deleteUnusedArticles() {

        return this.restService.deleteAll();
    }
}