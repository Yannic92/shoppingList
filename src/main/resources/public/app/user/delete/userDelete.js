shoppingList.controller('userDelete', ['$rootScope','$scope','userService','authService','$location',
    function ($rootScope, $scope, userService, authService, $location) {
        $rootScope.title = "Konto löschen";

        $scope.deleteAccount = function () {
            userService.delete($rootScope.user)
                .then(function () {
                    authService.logout()
                        .finally(function () {
                            $location.path("/login");
                        })
                })
        }
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/deleteAccount', {
        templateUrl: '/app/user/delete/userDelete.html',
        controller: 'userDelete'
    });
}]);