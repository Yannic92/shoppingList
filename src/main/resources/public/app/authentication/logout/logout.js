var shoppingList = shoppingList || angular.module('shoppingList', []);

shoppingList.controller('logout',['$scope', 'authService', '$rootScope',
    function($scope, authService, $rootScope) {
        authService.logout();
        
        $rootScope.title = "Logout";
    }
]);

shoppingList.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/logout', {
        templateUrl: '/app/authentication/logout/logout.html',
        controller: 'logout'
    });
}]);
