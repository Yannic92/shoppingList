shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/view/view.html',
        controller: 'listView'
    };
    $routeProvider
        .when('/lists/:listId', routeConfig);
}]);

shoppingList.controller('listView', ['$scope', '$rootScope','listService','$routeParams','$filter',
    function ($scope, $rootScope,listService,$routeParams,$filter) {
        'use strict';

        $rootScope.title = "Einkaufslisten";
        
        var lists = listService.get();
        
        $scope.list = {
            name: ''
        };
        
        $scope.updating = true;

        lists.promise
            .then(function () {
                if($routeParams.listId){
                    $scope.list = angular.copy($filter('filter')(lists, {entityId: $routeParams.listId})[0]);    
                }  
            }).finally(function () {
                $scope.updating = false;
            });
        
        $scope.update = function () {
            $scope.updating = true;
            listService.getUpdated($scope.list)
                .then(function(updatedList){
                    $scope.list = updatedList;
                }).finally(function () {
                    $scope.updating = false;
                });
        };
    }
]);