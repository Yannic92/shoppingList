shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/lists/lists.html',
        controller: 'lists'
    };
    $routeProvider
        .when('/lists', routeConfig)
}]);

shoppingList.controller('lists', ['$scope', '$rootScope','listService',
    function ($scope, $rootScope, listService) {
        'use strict';

        $rootScope.title = "Einkaufslisten";
        
        var lists = listService.get();
        
        lists.promise.then(function(){
            
            if(!$scope.listsAreEmpty()){
                $scope.$parent.goto("/lists/" + lists[0].entityId);
            }
        });
        
        $scope.listsAreEmpty = function(){
            return !lists || !lists.length || lists.length == 0;
        };
        
        $scope.goToFirstList = function (){
            
            
        }
    }
]);