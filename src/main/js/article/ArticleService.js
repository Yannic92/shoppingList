import HALResource from '../services/HALResource';
import Article from './Article';

export default class ArticleService {

    /*@ngInject*/
    constructor($resource, $filter, $q, $rootScope) {

        this.$filter = $filter;
        this.$q = $q;
        this.$rootScope = $rootScope;

        this.articlesResource = $resource('/articles/:id', null, {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });

        this.articles = [];
    }

    get() {
        if (!this.articles.fetching && !this.articles.fetched) {
            this.fetch();
        }
        return this.articles;
    }

    findByName(articleName) {
        const persistedArticlesWithSameName = this.$filter('filter')(this.articles, {name: articleName}, true);

        if(persistedArticlesWithSameName && persistedArticlesWithSameName[0]) {
            return persistedArticlesWithSameName[0];
        }

        return undefined;
    }

    findById(entityId) {
        const persistedArticlesWithSameId = this.$filter('filter')(this.articles, {entityId: entityId}, true);

        if(persistedArticlesWithSameId && persistedArticlesWithSameId[0]) {
            return persistedArticlesWithSameId[0];
        }

        return undefined;
    }

    create(article) {

        let persistedArticleWithSameName = this.findByName(article.name);

        if (persistedArticleWithSameName) {
            article.entityId = persistedArticleWithSameName.entityId;
            article._links = persistedArticleWithSameName._links;
            return this.$q((resolve) => resolve(article));
        }

        return this.articlesResource.save(article.toResource()).$promise
            .then((response) => {
                const article = Article.ofResource(response);
                this.articles.push(article);
                return article;
            });
    }

    update(article) {
        return this.articlesResource.update({id: article.entityId}, article.toResource()).$promise
            .then((response) => {
                const article = Article.ofResource(response);
                this._replaceExisting(article);
                return article;
            });
    }

    fetch() {
        if(!this.$rootScope.authenticated) {
            this.articles.promise = this._getRejectedPromise('Not authenticated');
        }else if (!this.articles.fetching) {
            this.articles.fetching = true;
            this.articles.promise = this.articlesResource.get().$promise
                .then((response) => {
                    const articles = ArticleService.toEntities(HALResource.getContent(response));
                    this.articles.splice(0, this.articles.length);
                    this.articles.push.apply(this.articles, articles);
                    this.articles.fetching = false;
                    this.articles.fetched = true;
                    return this.articles;
                });
        }
        return this.articles.promise;
    }

    delete(article) {
        return this.articlesResource.delete({id: article.entityId}).$promise
            .then(() => {
                const existingArticle = this.findById(article.entityId);
                if(existingArticle) {
                    const index = this.articles.indexOf(existingArticle);
                    this.articles.splice(index, 1);
                }
            });
    }

    deleteUnused() {
        return this.articlesResource.delete().$promise
            .then(() => {
                return this.fetch();
            });
    }

    _getRejectedPromise(message) {
        const deferred = this.$q.defer();
        const rejectedPromise = deferred.promise;
        deferred.reject(message);

        return rejectedPromise;
    }

    _replaceExisting(article) {

        const existingArticle = this.findById(article.entityId);
        if(existingArticle) {
            const index = this.articles.indexOf(existingArticle);
            this.articles.splice(index, 1, article);
        }else {
            this.articles.push(article);
        }
    }

    static toEntities(resources = []) {

        return resources.map((resource) => {
            return Article.ofResource(resource);
        });
    }
}