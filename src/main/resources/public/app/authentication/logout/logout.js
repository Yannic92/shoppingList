var shoppingList = shoppingList || angular.module('shoppingList', []);

shoppingList.controller('logout',['$scope', 'authService', '$rootScope','$window','$location',
    function($scope, authService, $rootScope,$window,$location) {
        authService.logout()
            .then(function () {
                $location.path("/login");
                $window.location.reload();
            });
        
        $rootScope.title = "Logout";
    }
]);

shoppingList.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/logout', {
        templateUrl: '/app/authentication/logout/logout.html',
        controller: 'logout'
    });
}]);
