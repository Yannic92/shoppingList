shoppingList.controller('newList', ['$scope','$rootScope','listService',
    function ($scope, $rootScope, listService) {
        $rootScope.title = "Neue Einkaufsliste";
        $scope.list = {
            name: ""
        };
        
        $scope.createList = function () {
            listService.create($scope.list);
        }
        
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/newList', {
        templateUrl: '/app/lists/new/newList.html',
        controller: 'newList'
    });
}]);