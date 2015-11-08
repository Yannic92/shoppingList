shoppingList.factory('userService',['$resource', 'HALResource',
    function($resource, HALResource){
    
        var userendpoint = 'api/sLUsers/:username';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var USERS = $resource(userendpoint, null, methods);
        var USER_CONFIRMATION = $resource(userendpoint + "/confirmation", null, methods);
        
        var userService = {
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
        return userService;
}]);