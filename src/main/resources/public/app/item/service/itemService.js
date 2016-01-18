shoppingList.factory('itemService',['$resource', 'HALResource','$filter','articleService',
    function($resource, HALResource,$filter,articleService){

        var itemsEndpoint = '/items/:id';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var Items = $resource(itemsEndpoint, null, methods);

        var toResource = function (entity) {

            var resource = {};

            resource._links = entity._links;
            resource.entityId = entity.entityId;
            resource.count = entity.count;
            resource.article = {entityId: entity.article.entityId};
            resource.done = entity.done;

            return resource;
        };

        var toEntity = function (resource){
            var entity = {};

            entity.entityId = resource.entityId;
            entity._links = resource._links;
            entity.count = resource.count;
            entity.article = resource.article;
            entity.done = resource.done;

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

        var replaceExisting = function (item) {

            var existingItem = $filter('filter')(persistedItems, {entityId: item.entityId})[0];
            var index = persistedItems.indexOf(existingItem);
            persistedItems.splice(index, 1, item);
        };

        var persistedItems = [];

        var itemService = {
            get: function(){
                return persistedItems;
            },
            create: function(item){

                return articleService.create(item.article).then(function(createdArticle){
                    item.article.entityId = createdArticle.entityId;
                    return Items.save(toResource(item)).$promise
                        .then(function(response){
                            var responseEntity = toEntity(response);
                            persistedItems.push(responseEntity);
                            return responseEntity;
                        })
                })

            },
            update: function (item) {
                return Items.update({id: item.entityId}, toResource(item)).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity;
                    })
            },
            fetch: function () {
                persistedItems.promise = Items.get().$promise
                    .then(function(response){
                        var entities = toEntities(HALResource.getContent(response));
                        persistedItems.splice(0, persistedItems.length);
                        persistedItems.push.apply(persistedItems, entities);
                        return entities;
                    });

                return persistedItems.promise;
            },
            delete: function (item) {
                return Items.delete({id: item.entityId}).$promise
                    .then(function(){
                        var existingList = $filter('filter')(persistedItems, {entityId: item.entityId})[0];
                        var index = persistedItems.indexOf(existingList);
                        persistedItems.splice(index,1);
                        return item;
                    });
            }
        };

        return itemService;
    }]);