shoppingList.controller('leftSideNav', ['$scope','listService','$mdSidenav','$mdDialog',
    function ($scope, listService, $mdSidenav, $mdDialog) {
        $scope.lists = listService.get();
        
        $scope.deleteList = function(list, ev){
            
            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + list.name+ "' wirklich löschen?")
                .content('Die Liste kann nicht wiederhergestellt werden.')
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            $mdDialog.show(confirm)
                .then(function () {
                    listService.delete(list);
                });
        }
    }
]);