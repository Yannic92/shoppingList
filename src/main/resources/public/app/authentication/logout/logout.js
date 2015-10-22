var shoppingList = shoppingList || angular.module('shoppingList', []);

shoppingList.controller('logout',['$scope', 'authService', '$location',
    function($scope, authService, $location) {
        authService.logout();

        $scope.goToLogin = function(){
            $location.path('/login');
        };
    }
]);

shoppingList.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/logout', {
        templateUrl: '/app/authentication/logout/logout.html',
        controller: 'logout'
    });
}]);
