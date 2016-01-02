shoppingList.controller('itemController', ['$scope','listService','itemService',
    function ($scope,listService,itemService) {
        'use strict';
        
        $scope.deleteItem = function(item, event) {

            itemService.delete(item)
                .then(function(){
                    var index = $scope.list.items.indexOf(item);
                    $scope.$parent.list.items.splice(index, 1);
                });

        };

        $scope.updateItem = function(item){

            itemService.update(item);
        };
    }
]);