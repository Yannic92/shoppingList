shoppingList.controller('register', ['$scope', '$rootScope', 'userService', '$location',
    function ($scope, $rootScope, userService, $location) {
        $rootScope.title = "Registrieren";
        $scope.loading = false;
        
        $scope.user = {
            authorities: [
                {
                    authority: "USER"
                }
            ]
        };

        $scope.register = function () {
            $scope.loading = true;
            userService.create($scope.user)
                .then(function () {
                    $location.path("/register/confirmation/" + $scope.user.username)
                }, function (error) {
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                    $rootScope.goToTop();
                })
                .finally(function(){
                    $scope.loading = false;
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
