shoppingList.factory('userService',['$resource', 'HALResource',
    function($resource, HALResource){

        var userendpoint = '/sLUsers/:username';
        var methods = {
            'update': { method:'PUT' },
            'delete': { method: 'DELETE'}
        };
        var USERS = $resource(userendpoint, null, methods);
        var USER_CONFIRMATION = $resource(userendpoint + "/confirmation", null, methods);
        var usersList = [];
        var fetched = false;
        var fetching = false;

        var userService = {
            get: function(){
                if(!fetched && !fetching){
                    userService.fetch();
                }
                return usersList;
            },
            storeCredentials : function(credentials){
                localStorage.setItem("credentials", JSON.stringify(credentials));

            },
            getCredentials: function(){
                return JSON.parse( localStorage.getItem("credentials") || '{}');
            },
            clearCredentials: function () {
                localStorage.setItem("credentials", JSON.stringify({}));
            },
            fetch: function(){
                fetching = true;
                usersList.promise = USERS.get().$promise
                    .then(function (response) {
                        usersList.splice(0, usersList.length);
                        usersList.push.apply(usersList, HALResource.getContent(response));
                        usersList.loaded = true;
                        fetched = true;
                        fetching = false;
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

        return userService;
}]);