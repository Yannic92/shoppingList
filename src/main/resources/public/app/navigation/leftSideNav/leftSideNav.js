shoppingList.controller('leftSideNav', ['$scope','listService','$mdSidenav',
    function ($scope, listService, $mdSidenav) {
        $scope.lists = listService.get();
    }
]);