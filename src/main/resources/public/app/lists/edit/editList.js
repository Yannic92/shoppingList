shoppingList.controller('editList', ['$scope','$rootScope','listService','$filter','$routeParams',
    function ($scope, $rootScope, listService,$filter,$routeParams) {
        $rootScope.title = "Einkaufsliste bearbeiten";
        lists = listService.get();
        $scope.list = {
            name: ''
        };
        
        lists.promise
            .then(function () {
                $scope.list = angular.copy($filter('filter')(lists, {id: $routeParams.id})[0]);         
            });
        
        $scope.updateList = function () {
            listService.update($scope.list);
        }
        
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/lists/:id/edit', {
        templateUrl: '/app/lists/edit/editList.html',
        controller: 'editList'
    });
}]);