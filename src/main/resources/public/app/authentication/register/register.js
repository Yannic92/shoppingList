shoppingList.controller('register', ['$scope', '$rootScope', 'userService', '$location',
    function ($scope, $rootScope, userService, $location) {
        $rootScope.title = "Registrieren";
        $rootScope.loading = false;

        $scope.user = {
            authorities: [
                {
                    authority: "USER"
                }
            ]
        };

        $scope.register = function () {
            $rootScope.loading = true;
            userService.create($scope.user)
                .then(function () {
                    $location.path("/register/confirmation");
                })
                .finally(function(){
                    $rootScope.loading = false;
                });
        };

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/register', {
        templateUrl: '/app/authentication/register/register.html',
        controller: 'register'
    });
}]);
