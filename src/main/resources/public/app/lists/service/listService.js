shoppingList.factory('listService',['$resource', 'HALResource','$filter','$q',
    function($resource, HALResource,$filter,$q){

        var listsEndpoint = '/shoppingLists/:id';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var Lists = $resource(listsEndpoint, null, methods);

        var toResource = function (entity) {
            var resource = {};
            resource._links = entity._links;
            resource.entityId = entity.entityId;
            resource.name = entity.name;
            resource.owners = [];
            resource.items = [];

            if(entity.owners) {
                for (var i = 0; i < entity.owners.length; i++) {
                    resource.owners.push({username: entity.owners[i].username});
                }
            }

            if(entity.items) {
                for (var i = 0; i < entity.items.length; i++) {
                    resource.items.push({entityId: entity.items[i].entityId});
                }
            }

            return resource;
        };

        var toEntity = function (resource){
            var entity = {};
            entity.entityId = resource.entityId;
            entity._links = resource._links;
            entity.name = resource.name;
            entity.owners = resource.owners;
            entity.items = resource.items;

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

        var replaceExisting = function (list) {
            var existingList = $filter('filter')(persistedLists, {entityId: list.entityId})[0];
            var index = persistedLists.indexOf(existingList);
            persistedLists.splice(index, 1, list);
        };

        var persistedLists = [];

        var listService = {
            get: function(){
                return persistedLists;
            },
            getUpdated: function(list) {
                return Lists.get({id: list.entityId}).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity;
                    });
            },
            create: function(list){
                return Lists.save(toResource(list)).$promise
                    .then(function(response){
                        var responseEntity = toEntity(response);
                        persistedLists.push(responseEntity);
                        return responseEntity;
                    })
            },
            update: function (list) {
                return Lists.update({id: list.entityId}, toResource(list)).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity;
                    })
            },
            fetch: function () {
                persistedLists.promise = Lists.get().$promise
                    .then(function(response){
                        var entities = toEntities(HALResource.getContent(response));
                        persistedLists.splice(0, persistedLists.length);
                        persistedLists.push.apply(persistedLists, entities);
                        return entities;
                    });

                return persistedLists.promise;
            },
            delete: function (list) {
                return Lists.delete({id: list.entityId}).$promise
                    .then(function(){
                        var existingList = $filter('filter')(persistedLists, {entityId: list.entityId})[0];
                        var index = persistedLists.indexOf(existingList);
                        persistedLists.splice(index,1);
                    });
            },
            deleteAll: function(){
                var promises = [];

                for(var i = 0; i < persistedLists.length; i++){
                    promises.push(listService.delete(persistedLists[i]));
                }

                return $q.all(promises);
            },
            getDeleteMessage: function(list){
                if(list && list.owners && list.owners.length > 1){
                    return "Die Liste wäre nur für dich nicht mehr verfügbar. Solltest du sie zurück wollen, " +
                        "müsstest du dich an die restlichen Nutzer der Liste wenden."
                }else{
                    return "Die Liste kann nicht wiederhergestellt werden."
                }
            }
        };

        listService.fetch();

        return listService;
    }]);