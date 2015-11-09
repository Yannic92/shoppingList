shoppingList.controller('editList', ['$scope','$rootScope','listService','$filter','$routeParams','$mdToast','$location',
    function ($scope, $rootScope, listService,$filter,$routeParams,$mdToast,$location) {
        $rootScope.title = "Einkaufsliste bearbeiten";
        var lists = listService.get();
        $scope.list = {
            name: ''
        };
        $scope.updating = true;
        
        lists.promise
            .then(function () {
                $scope.updating = false;
                $scope.list = angular.copy($filter('filter')(lists, {id: $routeParams.id})[0]);         
            });
        
        $scope.updateList = function () {
            $scope.updating = true;
            listService.update($scope.list)
                .then(function (updatedList) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Liste aktualisiert')
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                    $location.path("/lists/" + updatedList.id);
                }).finally(function () {
                    $scope.updating = false;
                });
        }
        
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/lists/:id/edit', {
        templateUrl: '/app/lists/edit/editList.html',
        controller: 'editList'
    });
}]);