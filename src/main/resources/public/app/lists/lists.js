shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/lists.html',
        controller: 'lists'
    };
    $routeProvider
        .when('/lists', routeConfig)
}]);

shoppingList.controller('lists', ['$scope', '$rootScope', '$mdDialog','listService',
    function ($scope, $rootScope, $mdDialog, listService) {
        'use strict';

        $rootScope.title = "Einkaufslisten";
        $rootScope.loading = true;

        $scope.lists = listService.get();

        var lists = $scope.lists;

        lists.promise.finally(function () {
            $rootScope.loading = false;
        });

        $scope.listsAreEmpty = function(){
            return !lists || !lists.length || lists.length == 0;
        };

        var initOptions = function () {
            $rootScope.options = [
                {
                    icon: "img/icons/content/ic_remove_circle_24px.svg",
                    text: "Alle Listen Löschen",
                    action: $scope.deleteAllLists,
                    disabled: $scope.listsAreEmpty
                }
            ];

            $rootScope.shortCutAction = {
                parameters: "$mdOpenMenu, $event",
                icon: "img/icons/action/ic_add_shopping_cart_24px.svg",
                action: $scope.newList,
                available: true,
                ariaLabel: "Neue Liste erstellen"
            }
        };

        $scope.newList = function(){
            $scope.$parent.goto("/newList");
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
                    return listService.delete(list);
                });
        };

        $scope.deleteAllLists = function (ev) {
            var confirm = $mdDialog.confirm()
                .title("Möchtest du wirklich alle Listen löschen?")
                .content("Wenn du der einzige Besitzer einer der Listen bist, wird diese Liste unwiderruflich gelöscht.")
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            return $mdDialog.show(confirm)
                .then(function () {
                    return listService.deleteAll();
                });
        };

        $scope.$on('$destroy', function(){
            $rootScope.reset();
        });

        initOptions();
    }
]);