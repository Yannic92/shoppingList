shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope','$window',
    function ($location ,$q, $injector, $rootScope,$window) {

        var sessionTimeOutCheck = false;
        var sessionTimeoutCheckPromise;

        var init = function(){
            var deferred = $q.defer();
            sessionTimeoutCheckPromise = deferred.promise;
            deferred.resolve("");
        };

        var handleSessionTimeout = function (rejection) {
            $rootScope.authenticated = true;
            var $mdDialog = $injector.get('$mdDialog');
            return $mdDialog.show(
                $mdDialog.alert()
                    .parent(angular.element(document.querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title('Sitzung abgelaufen')
                    .content('Ihre Sitzung ist abgelaufen. Bitte melden Sie sich erneut an.')
                    .ariaLabel('Sitzung abgelaufen')
                    .ok('OK')
            ).then(function(){
                $injector.get('authService').logout()
                    .then(function () {
                        $location.path("/login");
                    })
            }).finally(function(){
                return $q.reject(rejection);
            });
        };

        var checkForSessionTimeout = function (rejection) {
            sessionTimeOutCheck = true;
            return $injector.get('authService').isAuthenticated();
        };

        return {
            responseError: function(rejection) {
                if(rejection.status == 401 || (rejection.status == 403 && rejection.data.message.indexOf("CSRF") > -1)) {

                    if(sessionTimeOutCheck && ! (rejection.config.url == "sLUsers/current")) {
                        return sessionTimeoutCheckPromise
                            .then(function(){
                                var $http = $injector.get('$http');
                                return $http(rejection.config);
                            }, function(){
                                return handleSessionTimeout(rejection);
                            });
                    }else if($rootScope.authenticated && !sessionTimeOutCheck) {
                        sessionTimeoutCheckPromise = checkForSessionTimeout(rejection);
                        sessionTimeoutCheckPromise
                            .then(function(){
                                var $http = $injector.get('$http');
                                return $http(rejection.config);
                            }, function(){
                                return handleSessionTimeout(rejection);
                            });
                        return sessionTimeoutCheckPromise;
                    }else if(! $injector.get('authService').loggingIn) {
                        $location.path('/login').replace();
                    }
                }
                return $q.reject(rejection);
            }
        };
    }
]);