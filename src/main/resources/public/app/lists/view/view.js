shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/view/view.html',
        controller: 'listView'
    };
    $routeProvider
        .when('/lists/:listId', routeConfig);
}]);

shoppingList.controller('listView', ['$scope', '$rootScope','listService','itemService','$routeParams','$filter', 'articleService','$mdDialog', '$q',
    function ($scope, $rootScope,listService,itemService,$routeParams,$filter, articleService,$mdDialog, $q) {
        'use strict';

        var lists = listService.get();

        $scope.articles = articleService.get();

        $scope.list = {
            name: '',
            items: []
        };

        $scope.ctrl = $scope;

        $rootScope.loading = true;
        $scope.creating = false;

        var newItem = $scope.newItem = {
            count: "",
            article : {
                name: "",
                priceInEuro: 0
            }
        };

        var initNewItem = function () {
            newItem = $scope.newItem = {
                count: "",
                article : {
                    name: "",
                    priceInEuro: 0
                }
            };
        };

        lists.promise
            .then(function () {
                if($routeParams.listId){
                    $scope.list = $filter('filter')(lists, {entityId: $routeParams.listId})[0];

                    if(!$scope.list){
                        $scope.$parent.goto("/lists", true);
                        return;
                    }

                    if(!$scope.list.updated) {
                        return $scope.update().then(init);
                    }else{
                        init();
                    }

                }
            }).finally(function () {
                $rootScope.loading = false;
            });

        $scope.update = function () {
            $rootScope.loading = true;
            return listService.getUpdated($scope.list)
                .then(function(updatedList){
                    $scope.list = updatedList;
                }).finally(function () {
                    $rootScope.loading = false;
                });
        };

        $scope.createNewItem = function (ev) {

            $rootScope.errorMessage = '';
            $rootScope.error = false;

            if($scope.selectedArticle){

                $scope.newItem.article = $scope.selectedArticle;
            }

            $scope.newItem.article.name = $scope.newItem.article.name.trim();

            if($scope.newItem.article.name != '') {

                $scope.creating = true;

                $mdDialog.show({
                        controller: createNewItemController,
                        templateUrl: 'app/item/new/newItem.html',
                        parent: angular.element(document.body),
                        targetEvent: ev,
                        clickOutsideToClose: true,
                        fullscreen: false
                    })
                    .then(function (item) {
                        return itemService.create(item)
                            .then(function (createdItem) {
                                $scope.list.items.push(createdItem);
                                listService.update($scope.list)
                                    .then(function () {
                                        initNewItem();
                                    }, function () {

                                    });
                            }, function(error){
                                $rootScope.errorMessage = error.data.message;
                                $rootScope.error = true;
                            })
                    }).finally(function () {
                    $scope.creating = false;
                });
            }
        };

        $scope.listIsEmpty = function () {

            return $scope.list && (!$scope.list.items || !$scope.list.items.length || !$scope.list.items.length > 0);
        };

        $scope.deleteList = function(ev) {
            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + $scope.list.name+ "' wirklich löschen?")
                .content(listService.getDeleteMessage())
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            $mdDialog.show(confirm)
                .then(function () {
                    var index = lists.indexOf($scope.list);
                    listService.delete($scope.list)
                        .then(function () {
                            goToPreviousList(index);
                        });
                });
        };

        $scope.clearList = function(ev){

            var confirm = $mdDialog.confirm()
                .title("Möchtest du die Liste '" + $scope.list.name+ "' wirklich leeren?")
                .content("Alle Posten auf der Liste, die als erledigt markiert wurden, werden unwiderruflich gelöscht.")
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            return $mdDialog.show(confirm)
                .then(function () {
                    var promises = [];
                    var newItems = [];

                    for(var i = 0; i < $scope.list.items.length; i++){
                        if($scope.list.items[i].done) {
                            promises.push(itemService.delete($scope.list.items[i]));
                        }else{
                            newItems.push($scope.list.items[i]);
                        }
                    }


                    $scope.list.items.splice(0, $scope.list.items.length);
                    $scope.list.items.push.apply($scope.list.items, newItems);
                    promises.push(listService.update($scope.list));

                    return $q.all(promises);
                });
        };

        var goToPreviousList = function(lastIndex){
            if(lists && lists.length && lists.length > 0){
                var newIndex = lastIndex < lists.length ? lastIndex : lastIndex - 1;
                $scope.$parent.goto("/lists/" + lists[newIndex].entityId, true);
            }else{
                $scope.$parent.goto("/lists", true);
            }
        };

        $scope.$on('$destroy', function(){
            $rootScope.reset();
        });

        function createNewItemController($scope, $mdDialog) {

            $scope.newItem = angular.copy(newItem);

            $scope.cancel = function() {
                $mdDialog.cancel();
            };
            $scope.create = function() {
                $mdDialog.hide($scope.newItem);
            };
        }

        $scope.listDoesntContainsUndoneItems = function () {

            if($scope.listIsEmpty()){
                return true;
            }

            for(var i = 0; i < $scope.list.items.length; i++){
                if(!$scope.list.items[i].done){
                    return false;
                }
            }

            return true;
        };

        $scope.listDoesntContainsDoneItems = function () {

            if($scope.listIsEmpty()){
                return true;
            }

            for(var i = 0; i < $scope.list.items.length; i++){
                if($scope.list.items[i].done){
                    return false;
                }
            }

            return true;
        };

        $scope.setAllUndone = function(){

            for(var i = 0; i < $scope.list.items.length; i++){
                if($scope.list.items[i].done) {
                    $scope.list.items[i].done = false;
                    itemService.update($scope.list.items[i]);
                }
            }
        };

        $scope.setAllDone = function(){

            for(var i = 0; i < $scope.list.items.length; i++){
                if(!$scope.list.items[i].done) {
                    $scope.list.items[i].done = true;
                    itemService.update($scope.list.items[i]);
                }
            }
        };

        var init = function () {

            $rootScope.title = $scope.list.name;
            $rootScope.options = [
                {
                    icon: "/img/icons/Toggle/ic_check_box_24px.svg",
                    text: "Alles erledigt",
                    action: $scope.setAllDone,
                    disabled: $scope.listDoesntContainsUndoneItems
                },{
                    icon: "/img/icons/Toggle/ic_check_box_outline_blank_24px.svg",
                    text: "Nichts erledigt",
                    action: $scope.setAllUndone,
                    disabled: $scope.listDoesntContainsDoneItems
                },{
                    icon: "/img/icons/communication/ic_clear_all_24px.svg",
                    text: "Liste leeren",
                    action: $scope.clearList,
                    disabled: $scope.listDoesntContainsDoneItems
                },{
                    icon: "img/icons/action/ic_settings_24px.svg",
                    text: "Liste bearbeiten",
                    link: "#/lists/" + $scope.list.entityId + "/edit"
                },{
                    icon: "img/icons/action/ic_delete_24px.svg",
                    text: "Liste löschen",
                    action: $scope.deleteList
                }
            ];

            $rootScope.shortCutAction = {
                parameters: "$mdOpenMenu, $event",
                icon: "img/icons/notification/ic_sync_24px.svg",
                action: $scope.update,
                available: true,
                ariaLabel: "refetch current list from server"
            }
        }
    }
]);