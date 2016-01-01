shoppingList.controller('leftSideNav', ['$scope','listService','$mdSidenav','$mdDialog','$location',
    function ($scope, listService, $mdSidenav, $mdDialog, $location) {
        $scope.lists = listService.get();
        
        $scope.deleteList = function(list, ev){
            
            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + list.name+ "' wirklich löschen?")
                .content(listService.getDeleteMessage(list))
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            $mdDialog.show(confirm)
                .then(function () {
                    //var deletedList = $filter('filter')($scope.lists, {entityId: list.entityId})[0];
                    var index = $scope.lists.indexOf(list);
                    listService.delete(list)
                        .then(function(){
                            goToPreviousListIfDeletedCurrentlyShownList(list, index);
                        });
                });
        };

        var goToPreviousListIfDeletedCurrentlyShownList = function(deletedList, lastIndex){
            if($location.path() === "/lists/" + deletedList.entityId && lastIndex != -1) {
                if ($scope.lists && $scope.lists.length && $scope.lists.length > 0) {
                    var newIndex = lastIndex < $scope.lists.length ? lastIndex : lastIndex - 1;
                    $location.path("/lists/" + $scope.lists[newIndex].entityId);
                } else {
                    $location.path("/lists");
                }
            }
        };
    }
]);