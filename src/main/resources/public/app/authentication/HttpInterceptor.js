shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope','$window',
    function ($location ,$q, $injector, $rootScope,$window) {
        return {
            responseError: function(rejection) {
                if(rejection.status == 401 || rejection.status == 403) {
                    
                    if($rootScope.authenticated) {
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
                    }else{
                        $location.path('/login');
                    }
                }
                return $q.reject(rejection);
            }
        };
    }
]);