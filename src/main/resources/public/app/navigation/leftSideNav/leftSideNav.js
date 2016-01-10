shoppingList.controller('leftSideNav', ['$scope','listService','$mdSidenav','$mdDialog','$location',
    function ($scope, listService, $mdSidenav, $mdDialog, $location) {
        $scope.lists = listService.get();

        $scope.deleteAllLists = function (ev) {
            var confirm = $mdDialog.confirm()
                .title("Möchtest du wirklich alle Listen löschen?")
                .content("Wenn du der einzige Besitzer einer der Listen bist, wird diese Liste unwiderruflich gelöscht.")
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            $mdDialog.show(confirm)
                .then(function () {
                    listService.deleteAll()
                        .then(function(){
                            $location.path("/lists");
                        });
                });
        };

        $scope.deleteList = function(list, ev){

            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + list.name+ "' wirklich löschen?")
                .content(listService.getDeleteMessage(list))
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            $mdDialog.show(confirm)
                .then(function () {
                    var index = $scope.lists.indexOf(list);
                    listService.delete(list)
                        .then(function(){
                            goToPreviousListIfDeletedCurrentlyShownList(list, index);
                        });
                });
        };

        $scope.listsAreEmpty = function(){
            return !$scope.lists || !$scope.lists.length || $scope.lists.length == 0;
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