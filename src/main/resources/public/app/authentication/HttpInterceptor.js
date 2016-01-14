shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope','$window',
    function ($location ,$q, $injector, $rootScope,$window) {

        var handleSessionTimeout = function () {
            $rootScope.authenticated = true;
            var $mdDialog = $injector.get('$mdDialog');
            $mdDialog.show(
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
                $window.location.reload()
            });
        };

        var checkForSessionTimeout = function (rejection) {
            $rootScope.sessionTimeOutCheck = true;
            $injector.get('authService').isAuthenticated()
                .then(function(){
                    var $http = $injector.get('$http');
                    return $http(rejection.config);
                }, function(){
                    $rootScope.authenticated = false;
                });
        };

        return {
            responseError: function(rejection) {
                if(rejection.status == 401 || (rejection.status == 403 && rejection.data.message.indexOf("CSRF") > -1)) {

                    if($rootScope.sessionTimeOutCheck) {
                        handleSessionTimeout();
                    }else if($rootScope.authenticated) {
                        checkForSessionTimeout(rejection);
                    }else {
                        $location.path('/login');
                    }
                }
                return $q.reject(rejection);
            }
        };
    }
]);