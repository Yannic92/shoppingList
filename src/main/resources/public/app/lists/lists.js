shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/lists.html',
        controller: 'lists'
    };
    $routeProvider
        .when('/lists', routeConfig)
        .when('/lists/:listId', routeConfig);
}]);

shoppingList.controller('lists', ['$scope', '$rootScope',
    function ($scope, $rootScope) {
        'use strict';
        
        $rootScope.title = "Einkaufslisten";
    }
]);