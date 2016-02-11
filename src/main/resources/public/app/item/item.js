shoppingList.controller('itemController', ['$scope','listService','itemService','$timeout','$rootScope',
    function ($scope,listService,itemService, $timeout, $rootScope) {
        'use strict';

        $scope.ctrl = $scope;
        $scope.optionsVisible = false;
        var optionsVisibleStopped = false;

        $scope.showOptionsOnElement = function () {
            if(!optionsVisibleStopped) {
                $scope.optionsVisible = true;
            }
        };

        $scope.hideOptionsOnElement = function () {
            optionsVisibleStopped = true;
            $scope.optionsVisible = false;

            $timeout(function(){
                optionsVisibleStopped = false;
            },15);
        };

        $scope.showOptions = function () {
            $scope.optionActive = true;

            $timeout(function(){
                $scope.optionsShown = true;
                $scope.hideOptionsOnElement();
            }, 20)
        };

        $scope.hideOptions = function () {
            $scope.optionsShown = false;
        };

        $scope.$watch('optionsShown', function(newValue, oldValue){

            if(oldValue && !newValue){
                $timeout(function(){
                    $scope.optionActive = false;
                    $scope.hideOptionsOnElement();
                });
            }
        });

        $scope.deleteItem = function(item) {

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