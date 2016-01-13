shoppingList.factory('authService',['$http', '$rootScope', 'userService', function($http, $rootScope, userService){
    var USER_ENDPOINT = 'sLUsers/current';

    var authService = {
        authenticate : function (credentials) {
            var headers = authService.getAuthenticationHeader(credentials);

            return $http.get(USER_ENDPOINT, {
                headers: headers
            }).then(function(response){
                if(response.data && response.data.username){
                    $rootScope.authenticated = true;
                    return response.data;
                }else {
                    $rootScope.authenticated = false;
                }
            });
        },
        isAuthenticated : function(){
            $rootScope.authenticationAlreadyChecked = true;
            var credentials = userService.getCredentials();
            if(credentials && credentials.username){
                return authService.authenticate(credentials);
            }

            return $http.get(USER_ENDPOINT)
                .then(function(response){
                    if(response.data.username){
                        $rootScope.authenticated = true;
                        return response.data;
                    }else{
                        $rootScope.authenticated = false;
                    }
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
                userService.clearCredentials();
            }).error(function () {
                $rootScope.authenticated = false;
                $rootScope.headers = {};
                $rootScope.user = '';
                userService.clearCredentials();
            });
        }
    };
    return authService;
}]);