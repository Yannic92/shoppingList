shoppingList.controller('listMenu', ['$scope','$mdDialog','listService',
    function ($scope,$mdDialog,listService) {
        $scope.openMenu = function($mdOpenMenu, ev) {
            originatorEv = ev;
            $mdOpenMenu(ev);
        };

        $scope.deleteList = function(ev) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + $scope.$parent.list.name+ "' wirklich löschen?")
                .content('Die Liste kann nicht wiederhergestellt werden.')
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');
            
            $mdDialog.show(confirm)
                .then(function () {
                    listService.delete($scope.$parent.list);
                });
        };
    }
]);