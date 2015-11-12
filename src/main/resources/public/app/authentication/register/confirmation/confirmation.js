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
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/register/confirmation/:username', {
        templateUrl: '/app/authentication/register/confirmation/confirmation.html',
        controller: 'confirmation'
    });
}]);
