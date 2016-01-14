shoppingList.factory('authService',['$http', '$rootScope', 'userService', function($http, $rootScope, userService){
    var USER_ENDPOINT = 'sLUsers/current';

    var handleSuccessfulLogin = function(response){
        if(response.data && response.data.username){
            $rootScope.authenticated = true;
            return response.data;
        }else {
            $rootScope.authenticated = false;
        }
    };

    var authService = {
        authenticate : function (credentials) {
            var headers = authService.getAuthenticationHeader(credentials);
            $rootScope.authenticationAlreadyChecked = true;
            return $http.get(USER_ENDPOINT, {
                headers: headers
            }).then(function(){
                return $http.get(USER_ENDPOINT)
                    .then(handleSuccessfulLogin);
            });
        },
        isAuthenticated : function(){
            $rootScope.authenticationAlreadyChecked = true;
            var credentials = userService.getCredentials();
            if(credentials && credentials.username){
                return authService.authenticate(credentials);
            }

            return $http.get(USER_ENDPOINT)
                .then(handleSuccessfulLogin);
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