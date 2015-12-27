shoppingList.factory('authService',['$http', '$rootScope', function($http, $rootScope){
    var USER_ENDPOINT = 'sLUsers/current';
    
    var authService = {
        authenticate : function (credentials) {
            var headers = authService.getAuthenticationHeader(credentials);

            return $http.get(USER_ENDPOINT, {
                headers: headers
            }).then(function(){
                return authService.isAuthenticated();
            });
        },
        isAuthenticated : function(){
            return $http.get(USER_ENDPOINT)
                .then(function(response){
                    if(response.data.username){
                        $rootScope.authenticated = true;
                        return response.data;
                    }else{
                        $rootScope.authenticated = false;
                    }
                }, function () {
                    $rootScope.authenticated = false;
                });
        },
        getAuthenticationHeader : function(credentials){
            return credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":"
                + credentials.password)
            } : {};
        },

        logout : function() {
            return $http.post('/logout', {}).success(function () {
                $rootScope.authenticated = false;
                $rootScope.headers = {};
                $rootScope.user = '';
            }).error(function () {
                $rootScope.authenticated = false;
                $rootScope.headers = {};
                $rootScope.user = '';
            });
        }
    };
    return authService;
}]);