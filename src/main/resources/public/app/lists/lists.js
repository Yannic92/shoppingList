shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/lists.html',
        controller: 'lists'
    };
    $routeProvider
        .when('/lists', routeConfig)
}]);

shoppingList.controller('lists', ['$rootScope',
    function ($rootScope) {
        'use strict';

        $rootScope.title = "Einkaufslisten";
    }
]);