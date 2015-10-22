shoppingList.controller('confirmation',[ '$scope', '$rootScope', 'userService', '$routeParams','$location',
    function ($scope, $rootScope, userService, $routeParams, $location) {
        $rootScope.title="Registrierung bestätigen";
        var username = $routeParams.username;
        
        $scope.confirmation = {
            code: ''
        };

        $scope.confirm = function () {
            
            userService.confirmRegistrationFor(username, $scope.confirmation)
                .then(function(){
                    $location.path("/login");
                },function(error){
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                    $rootScope.goToTop();
                }
            );
        }
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/register/confirmation/:username', {
        templateUrl: '/app/authentication/register/confirmation/confirmation.html',
        controller: 'confirmation'
    });
}]);
