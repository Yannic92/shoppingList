shoppingList.controller('userDataSuccess', ['$rootScope',
    function ($rootScope) {
        $rootScope.title = "Erfolgreich aktualisiert";
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/userDataSuccess', {
        templateUrl: '/app/user/success/userDataSuccess.html',
        controller: 'userDataSuccess'
    });
}]);