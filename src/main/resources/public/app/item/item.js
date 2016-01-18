shoppingList.controller('itemController', ['$scope','listService','itemService',
    function ($scope,listService,itemService) {
        'use strict';

        $scope.deleteItem = function(item, event) {

            item.deleting = true;
            itemService.delete(item)
                .then(function(){
                    var index = $scope.list.items.indexOf(item);
                    $scope.$parent.list.items.splice(index, 1);
                    return item;
                })
                .finally(function () {
                    item.deleting = false;
                });

        };

        $scope.updateItem = function(item){

            itemService.update(item);
        };
    }
]);