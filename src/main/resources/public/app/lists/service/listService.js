shoppingList.factory('listService',['$resource', 'HALResource','$filter','$q',
    function($resource, HALResource,$filter,$q){

        var listsEndpoint = 'api/shoppingLists/:id';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var Lists = $resource(listsEndpoint, null, methods);

        var toResource = function (entity) {
            var resource = {};
            resource._links = entity._links;
            resource.id = entity.id;
            resource.name = entity.name;
            resource.owners = [];
            
            for(var i = 0; i < entity.owners.length ; i++){
                resource.owners.push(entity.owners[i]._links.self.href);
            }
            
            return resource;
        };
        
        var toEntity = function (resource){
            var entity = {};
            entity.id = resource.id;
            entity._links = resource._links;
            entity.name = resource.name;
            entity.owners = [];
            
            entity.promise = $resource(resource._links.owners.href).get().$promise
                .then(function(response){
                    entity.owners.push.apply(entity.owners, HALResource.getContent(response));
                    return entity;
                });
            
            return entity;
        };
        
        var toEntities = function(resources){
            
            var entities = [];
            if(!resources || !resources.length){
                return entities;
            }
            
            var promises = [];
            
            for(var i = 0 ; i < resources.length; i++){
                var entity = toEntity(resources[i]);
                entities.push(entity);
                promises.push(entity.promise);
            }
            
            entities.promise = $q.all(promises);
            
            return entities;
        };
        
        var replaceExisting = function (list) {
            var existingList = $filter('filter')(persistedLists, {id: list.id})[0];
            var index = persistedLists.indexOf(existingList);
            persistedLists.splice(index, 1, list);
        };
        
        var persistedLists = [];
        
        var listService = {
            get: function(){
                return persistedLists;
            },
            getUpdated: function(list) {
                return Lists.get({id: list.id}).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity.promise;
                    });
            },
            create: function(list){
                return Lists.save(toResource(list)).$promise
                    .then(function(response){
                        var responseEntity = toEntity(response);
                        persistedLists.push(responseEntity);
                        return responseEntity.promise;
                    })
            },
            update: function (list) {
                return Lists.update({id: list.id}, toResource(list)).$promise
                    .then(function (response) {
                        var responseEntity = toEntity(response);
                        replaceExisting(responseEntity);
                        return responseEntity.promise;
                    })
            },
            fetch: function () {
                persistedLists.promise = Lists.get().$promise
                    .then(function(response){
                        var entities = toEntities(HALResource.getContent(response));
                        persistedLists.splice(0, persistedLists.length);
                        persistedLists.push.apply(persistedLists, entities);
                        return entities.promise;
                    });
                
                return persistedLists.promise;
            },
            delete: function (list) {
                return Lists.delete({id: list.id}).$promise
                    .then(function(){
                        var existingList = $filter('filter')(persistedLists, {id: list.id})[0];
                        var index = persistedLists.indexOf(existingList);
                        persistedLists.splice(index,1);
                    });
            }
        };
        
        listService.fetch();
        
        return listService;
    }]);