shoppingList.factory('articleService',['$resource', 'HALResource','$filter', '$q','$rootScope',
    function($resource, HALResource,$filter, $q, $rootScope){

        var articlesEndpoint = '/articles/:id';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var Articles = $resource(articlesEndpoint, null, methods);

        var deferred =$q.defer();
        var rejectedPromise = deferred.promise;
        deferred.reject("");

        var toResource = function (entity) {

            var resource = {};

            resource._links = entity._links;
            resource.entityId = entity.entityId;
            resource.name = entity.name;
            resource.priceInEuro = entity.priceInEuro;

            return resource;
        };

        var toEntity = function (resource){
            var entity = {};

            entity.entityId = resource.entityId;
            entity._links = resource._links;
            entity.name = resource.name;
            entity.priceInEuro = resource.priceInEuro;

            return entity;
        };

        var toEntities = function(resources){

            var entities = [];
            if(!resources || !resources.length){
                return entities;
            }

            for(var i = 0 ; i < resources.length; i++){
                var entity = toEntity(resources[i]);
                entities.push(entity);
            }

            return entities;
        };

        var replaceExisting = function (article) {

            var existingArticle = $filter('filter')(persistedArticles, {entityId: article.entityId})[0];
            var index = persistedArticles.indexOf(existingArticle);
            persistedArticles.splice(index, 1, article);
        };

        var persistedArticles = [];

        var articleService = {
            get: function(){
                if(!persistedArticles.fetching && !persistedArticles.fetched){
                    articleService.fetch();
                }
                return persistedArticles;
            },
            create: function(article){
                if (article) {
                    var persistedArticlesWithSameName = $filter('filter')(persistedArticles, {name: article.name}, true);
                    if (persistedArticlesWithSameName && persistedArticlesWithSameName[0]) {
                        article.entityId = persistedArticlesWithSameName[0].entityId;
                        return $q(function(resolve, reject) {
                            resolve(article);
                        });
                    }
                }

                return Articles.save(toResource(article)).$promise
                    .then(function(response){
                        var responseEntity = toEntity(response);
                        persistedArticles.push(responseEntity);
                        return responseEntity;
                    })
            },
            update: function (article) {
                return Articles.update({id: article.entityId}, toResource(article)).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity;
                    })
            },
            fetch: function () {
                if($rootScope.authenticated) {
                    persistedArticles.promise = Articles.get().$promise
                        .then(function (response) {
                            var entities = toEntities(HALResource.getContent(response));
                            persistedArticles.splice(0, persistedArticles.length);
                            persistedArticles.push.apply(persistedArticles, entities);
                            return entities;
                        });

                    return persistedArticles.promise;
                }else{
                    persistedArticles.promise = rejectedPromise;
                }

                return rejectedPromise;
            },
            delete: function (article) {
                return Articles.delete({id: article.entityId}).$promise
                    .then(function(){
                        var existingList = $filter('filter')(persistedArticles, {entityId: article.entityId})[0];
                        var index = persistedArticles.indexOf(existingList);
                        persistedArticles.splice(index,1);
                    });
            },
            deleteUnused: function(){
                return Articles.delete().$promise
                    .then(function(){
                        return articleService.fetch();
                    });
            }
        };

        return articleService;
    }]);