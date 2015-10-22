shoppingList.service('MyHttpInterceptor',['$location', '$q', '$injector', '$rootScope',
    function ($location ,$q, $injector, $rootScope) {
        return {
            responseError: function(rejection) {
                if(rejection.status == 401 || rejection.status == 403) {
                    
                    if($rootScope.authenticated) {
                        console.log("Session timed out")
                    }else{
                        $location.path('/login');
                    }
                }
                return $q.reject(rejection);
            }
        };
    }
]);