shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/view/view.html',
        controller: 'listView'
    };
    $routeProvider
        .when('/lists/:listId', routeConfig);
}]);

shoppingList.controller('listView', ['$scope', '$rootScope','listService','itemService','$routeParams','$filter', 'articleService','$mdDialog',
    function ($scope, $rootScope,listService,itemService,$routeParams,$filter, articleService,$mdDialog) {
        'use strict';
        
        var lists = listService.get();
        
        $scope.articles = articleService.get();
        
        $scope.list = {
            name: '',
            items: []
        };
        
        $scope.ctrl = $scope;
        
        $scope.updating = true;
        
        $scope.newItem = {
            article : {
                name: "",
                priceInEuro: 0
            }
        };
        
        var initNewItem = function () {
            $scope.newItem = {
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
                        $scope.$parent.goto("/lists");
                        return;
                    }
                    $rootScope.title = $scope.list.name;
                    $rootScope.options = [
                        {
                            icon: "settings",
                            text: "Liste Bearbeiten",
                            link: "#/lists/" + $scope.list.entityId + "/edit"
                        },{
                            icon: "delete",
                            text: "Liste löschen",
                            action: $scope.deleteList
                        }
                    ];
                    
                    $rootScope.shortCutAction = {
                        parameters: "$mdOpenMenu, $event",
                        icon: "sync",
                        action: $scope.update,
                        available: true
                    }
                }  
            }).finally(function () {
                $scope.updating = false;
            });
        
        $scope.update = function () {
            $scope.updating = true;
            listService.getUpdated($scope.list)
                .then(function(updatedList){
                    $scope.list = updatedList;
                }).finally(function () {
                    $scope.updating = false;
                });
        };
        
        $scope.createNewItem = function () {
            
            if($scope.selectedArticle){
                
                $scope.newItem.article = $scope.selectedArticle;
            }
            
            $scope.newItem.count = 1;
            
            itemService.create($scope.newItem)
                .then(function(createdItem){
                    $scope.list.items.push(createdItem);
                    listService.update($scope.list)
                        .then(function(){
                            initNewItem();
                        },function(){
                            
                        });
                })
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
        
        var goToPreviousList = function(lastIndex){
            if(lists && lists.length && lists.length > 0){
                var newIndex = lastIndex < lists.length ? lastIndex : lastIndex - 1;
                $scope.$parent.goto("/lists/" + lists[newIndex].entityId);
            }else{
                $scope.$parent.goto("/lists");
            }
        };
        
        $scope.$on('$destroy', function(){  
            $rootScope.reset();
        });
    }
]);