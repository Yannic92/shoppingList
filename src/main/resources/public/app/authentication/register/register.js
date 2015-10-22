shoppingList.controller('register', ['$scope', '$rootScope', 'userService', '$location',
    function ($scope, $rootScope, userService, $location) {
        $rootScope.title = "Registrieren";

        $scope.user = {
            authorities: [
                {
                    authority: "USER"
                }
            ]
        };

        $scope.register = function () {

            userService.create($scope.user)
                .then(function () {
                    $location.path("/register/confirmation/" + $scope.user.username)
                }, function (error) {
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                    $rootScope.goToTop();
                });
        };
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/register', {
        templateUrl: '/app/authentication/register/register.html',
        controller: 'register'
    });
}]);
