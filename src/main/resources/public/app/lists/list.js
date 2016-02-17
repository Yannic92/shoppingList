shoppingList.controller('listController', ['$scope','listService','$timeout','$mdDialog',
    function ($scope,listService, $timeout, $mdDialog) {
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
    }
]);