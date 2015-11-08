shoppingList.controller('userMenu', ['$scope',
    function ($scope) {
        $scope.openMenu = function($mdOpenMenu, ev) {
            originatorEv = ev;
            $mdOpenMenu(ev);
        };
    }
]);