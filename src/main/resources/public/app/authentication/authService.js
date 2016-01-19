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
        loggingOut: false,
        loggedOut: false,
        loggingIn: false,
        authenticate : function (credentials) {
            authService.loggingIn = true;
            var headers = authService.getAuthenticationHeader(credentials);
            $rootScope.authenticationAlreadyChecked = true;
            return $http.get(USER_ENDPOINT, {
                headers: headers
            }).then(function(){
                return $http.get(USER_ENDPOINT)
                    .then(handleSuccessfulLogin)
                    .finally(function(){
                        authService.loggingIn = false;
                    });
            });
        },
        isAuthenticated : function(){
            authService.loggingIn = true;
            $rootScope.authenticationAlreadyChecked = true;
            var credentials = userService.getCredentials();
            if(credentials && credentials.username){
                return authService.authenticate(credentials);
            }

            return $http.get(USER_ENDPOINT)
                .then(handleSuccessfulLogin)
                .finally(function () {
                    authService.loggingIn = false;
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
            authService.loggingOut = true;
            return $http.post('/logout', {}).finally(function(){
                $rootScope.authenticated = false;
                $rootScope.headers = {};
                $rootScope.user = '';
                userService.clearCredentials();
            });
        }
    };
    return authService;
}]);