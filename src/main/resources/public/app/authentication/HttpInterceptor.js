shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope','$window',
    function ($location ,$q, $injector, $rootScope,$window) {

        var sessionTimeOutCheck = false;
        var sessionTimeoutCheckPromise;

        var connectionLossNotification = false;
        var connectionLossPromise;

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
                $rootScope.error = true;
                $rootScope.errorMessage = "Verbindung fehlgeschlagen";
                $rootScope.goToTop();
                return $q.reject(rejection);
            });
        };

        var handleOfflineRequest = function(rejection){
            var $mdDialog = $injector.get('$mdDialog');
            return $mdDialog.show(
                $mdDialog.confirm()
                    .title("Verbindung fehlgeschlagen")
                    .content("Entweder besteht aktuell keine Verbindung zum Internet, oder der Dient wird gewartet. Möchtest du es erneut versuchen?")
                    .ok('Ja')
                    .cancel('Nein')
            ).then(function () {
                connectionLossNotification = false;
            });
        };

        var checkForSessionTimeout = function (rejection) {
            sessionTimeOutCheck = true;
            return $injector.get('authService').isAuthenticated();
        };

        return {
            responseError: function(rejection) {

                if(rejection.status <= 0 && ! connectionLossNotification){
                    connectionLossNotification = true;
                    connectionLossPromise = handleOfflineRequest(rejection);
                    connectionLossPromise
                        .then(function(){
                            var $http = $injector.get('$http');
                            connectionLossNotification = false;
                            return $http(rejection.config);
                        }, function(){
                            connectionLossNotification = false;
                            return $q.reject(rejection);
                        });
                    return connectionLossPromise;
                }else if(rejection.status <= 0 && connectionLossNotification){
                    return connectionLossPromise
                        .then(function(){
                            var $http = $injector.get('$http');
                            return $http(rejection.config);
                        }, function(){
                            return $q.reject(rejection);
                        })
                }
                else if(rejection.status == 401 || (rejection.status == 403 && rejection.data.message.indexOf("CSRF") > -1)) {

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
                        return sessionTimeoutCheckPromise
                            .then(function(){
                                var $http = $injector.get('$http');
                                return $http(rejection.config);
                            }, function(){
                                return handleSessionTimeout(rejection);
                            });
                    }else if(! $injector.get('authService').loggingIn) {
                        $location.path('/login').replace();
                    }
                }else if(rejection.status == 400 || rejection.status == 404){
                    $rootScope.error = true;
                    $rootScope.errorMessage = rejection.data.message;
                    $rootScope.goToTop();
                }else {
                    $rootScope.error = true;
                    $rootScope.errorMessage = "Sorry! Etwas ging schief. Bitte versuche es später erneut";
                    $rootScope.goToTop();
                }
                return $q.reject(rejection);
            }
        };
    }
]);