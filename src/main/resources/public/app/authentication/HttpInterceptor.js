shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope','$window',
    function ($location ,$q, $injector, $rootScope,$window) {

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
                $rootScope.authenticated = false;
                $location.path("/login");
                $window.location.reload();
            }).finally(function(){
                return $q.reject(rejection);
            });
        };

        var checkForSessionTimeout = function (rejection) {
            $rootScope.sessionTimeOutCheck = true;
            return $injector.get('authService').isAuthenticated()
                .then(function(){
                    var $http = $injector.get('$http');
                    return $http(rejection.config);
                }, function(){
                    $rootScope.authenticated = false;
                    return $q.reject(rejection);
                });
        };

        return {
            responseError: function(rejection) {
                if(rejection.status == 401 || (rejection.status == 403 && rejection.data.message.indexOf("CSRF") > -1)) {

                    if($rootScope.sessionTimeOutCheck) {
                        return handleSessionTimeout();
                    }else if($rootScope.authenticated) {
                        return checkForSessionTimeout(rejection);
                    }else {
                        $location.path('/login');
                    }
                }
                return $q.reject(rejection);
            }
        };
    }
]);