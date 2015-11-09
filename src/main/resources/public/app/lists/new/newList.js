shoppingList.controller('newList', ['$scope','$rootScope','listService','$mdToast','$location',
    function ($scope, $rootScope, listService,$mdToast,$location) {
        
        $rootScope.title = "Neue Einkaufsliste";
        $scope.loading = false;
        
        $scope.list = {
            name: ""
        };
        
        $scope.createList = function () {
            $scope.loading = true;
            listService.create($scope.list)
                .then(function (createdList) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Neue Liste erstellt')
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                    $location.path("/lists/" + createdList.id);
                })
                .finally(function () {
                    $scope.loading = false;
                });
        }
        
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/newList', {
        templateUrl: '/app/lists/new/newList.html',
        controller: 'newList'
    });
}]);