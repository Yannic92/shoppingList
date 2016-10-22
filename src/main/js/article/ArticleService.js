import HALResource from '../services/HALResource';
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

    create(article) {
        if (article) {
            let persistedArticlesWithSameName = this.$filter('filter')(this.articles, {name: article.name}, true);
            if (persistedArticlesWithSameName && persistedArticlesWithSameName[0]) {
                article.entityId = persistedArticlesWithSameName[0].entityId;
                return this.$q((resolve) => {
                    resolve(article);
                });
            }
        }

        return this.articlesResource.save(ArticleService.toResource(article)).$promise
            .then((response) => {
                var responseEntity = ArticleService.toEntity(response);
                this.articles.push(responseEntity);
                return responseEntity;
            });
    }

    update(article) {
        return this.articlesResource.update({id: article.entityId}, ArticleService.toResource(article)).$promise
            .then((response) => {
                var responseEntity = ArticleService.toEntity(response);
                this._replaceExisting(responseEntity);
                return responseEntity;
            });
    }

    fetch() {
        if (this.$rootScope.authenticated && !this.articles.fetching) {
            this.articles.fetching = true;
            this.articles.promise = this.articlesResource.get().$promise
                .then((response) => {
                    var entities = ArticleService.toEntities(HALResource.getContent(response));
                    this.articles.splice(0, this.articles.length);
                    this.articles.push.apply(this.articles, entities);
                    this.articles.fetching = false;
                    this.articles.fetched = true;
                    return entities;
                });
        } else {
            this.articles.promise = this._getRejectedPromise('Not authenticated');
        }

        return this.articles.promise;
    }

    delete(article) {
        return this.articlesResource.delete({id: article.entityId}).$promise
            .then(() => {
                var existingList = this.$filter('filter')(this.articles, {entityId: article.entityId})[0];
                var index = this.articles.indexOf(existingList);
                this.articles.splice(index, 1);
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

        var existingArticle = this.this.$filter('filter')(this.articles, {entityId: article.entityId})[0];
        var index = this.articles.indexOf(existingArticle);
        this.articles.splice(index, 1, article);
    }

    static toResource(entity) {

        var resource = {};

        resource._links = entity._links;
        resource.entityId = entity.entityId;
        resource.name = entity.name;
        resource.priceInEuro = entity.priceInEuro;

        return resource;
    }

    static toEntity(resource) {
        var entity = {};

        entity.entityId = resource.entityId;
        entity._links = resource._links;
        entity.name = resource.name;
        entity.priceInEuro = resource.priceInEuro;

        return entity;
    }

    static toEntities(resources) {

        var entities = [];
        if (!resources || !resources.length) {
            return entities;
        }

        for (var i = 0; i < resources.length; i++) {
            var entity = this.toEntity(resources[i]);
            entities.push(entity);
        }

        return entities;
    }
}