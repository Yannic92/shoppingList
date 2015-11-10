shoppingList.factory('userService',['$resource', 'HALResource',
    function($resource, HALResource){
    
        var userendpoint = 'api/sLUsers/:username';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var USERS = $resource(userendpoint, null, methods);
        var USER_CONFIRMATION = $resource(userendpoint + "/confirmation", null, methods);
        var usersList = [];
        
        var userService = {
            get: function(){
                return usersList;
            },
            fetch: function(){
                usersList.promise = USERS.get().$promise
                    .then(function (response) {
                        usersList.splice(0, usersList.length);
                        usersList.push.apply(usersList, HALResource.getContent(response));
                        usersList.loaded = true;
                        return usersList;
                    });
                
                return usersList.promise;
            },
            create: function(user){
                return USERS.save(user).$promise
                    .then(function(response){
                        return HALResource.getContent(response);
                    })
            },
            update : function(user){
                return USERS.update({username: user.username}, user).$promise
                    .then(function(updatedUser){
                        return HALResource.getContent(updatedUser);
                    });
            },
            delete: function (user) {
                return USERS.delete({username: user.username}).$promise;
            },
            confirmRegistrationFor : function(username, confirmation){
                return USER_CONFIRMATION.update({username: username}, confirmation).$promise;
            }
        };
        
        userService.fetch();
        
        return userService;
}]);