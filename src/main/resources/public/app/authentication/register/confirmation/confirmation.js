shoppingList.controller('confirmation',[ '$scope', '$rootScope', 'userService', '$routeParams','$location',
    function ($scope, $rootScope, userService, $routeParams, $location) {

        $rootScope.title="Konto aktivieren";
        $rootScope.loading = true;
        $scope.success = false;
        var username = $routeParams.username;

        $scope.confirmation = {
            code: $routeParams.code ? $routeParams.code : ''
        };

        var confirm = function () {
            userService.confirmRegistrationFor(username, $scope.confirmation)
                .then(function(){
                    $scope.success = true;
                },function(error){
                    $scope.success = false;
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                }).finally(function () {
                $rootScope.loading = false;
            });
        };
        
        confirm();

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

    $routeProvider.when('/register/confirmation/:username/:code', routeConfig);
}]);
