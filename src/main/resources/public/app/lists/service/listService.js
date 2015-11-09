shoppingList.factory('listService',['$resource', 'HALResource','$filter',
    function($resource, HALResource,$filter){

        var listsEndpoint = 'api/shoppingLists/:id';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var Lists = $resource(listsEndpoint, null, methods);

        var toResource = function (entity) {
            var resource = {};
            resource._links = entity._links;
            resource.name = entity.name;
            resource.owners = entity.owners;
            
            return resource;
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
                        replaceExisting(response);
                        return response;
                    });
            },
            create: function(list){
                return Lists.save(list).$promise
                    .then(function(response){
                        persistedLists.push(response);
                        return response;
                    })
            },
            update: function (list) {
                return Lists.update({id: list.id}, list).$promise
                    .then(function (response) {
                        replaceExisting(response);
                        return response;
                    })
            },
            fetch: function () {
                persistedLists.promise = Lists.get().$promise
                    .then(function(response){
                        persistedLists.splice(0, persistedLists.length);
                        persistedLists.push.apply(persistedLists, HALResource.getContent(response));
                        return persistedLists;
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