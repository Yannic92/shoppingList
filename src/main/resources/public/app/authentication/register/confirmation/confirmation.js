shoppingList.controller('confirmation',[ '$scope', '$rootScope', 'userService', '$routeParams','$location',
    function ($scope, $rootScope, userService, $routeParams, $location) {
        
        $rootScope.title="Registrierung bestätigen";
        $scope.loading = false;
        var username = $routeParams.username;
                
        $scope.confirmation = {
            code: $routeParams.code ? $routeParams.code : ''
        };

        $scope.confirm = function () {
            $scope.loading = true;
            userService.confirmRegistrationFor(username, $scope.confirmation)
                .then(function(){
                    $location.path("/login");
                },function(error){
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                    $rootScope.goToTop();
                }).finally(function () {
                    $scope.loading = false;
                });
        }

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/authentication/register/confirmation/confirmation.html',
        controller: 'confirmation'
    };
    
    $routeProvider.when('/register/confirmation/:username', routeConfig)
        .when('/register/confirmation/:username/:code', routeConfig);
}]);
