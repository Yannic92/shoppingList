shoppingList.controller('editList', ['$scope','$rootScope','listService','$filter','$routeParams','$mdToast','$location','$mdDialog','userService',
    function ($scope, $rootScope, listService,$filter,$routeParams,$mdToast,$location,$mdDialog,userService) {
        $rootScope.title = "Einkaufsliste bearbeiten";
        var lists = listService.get();
        $scope.list = {
            name: ''
        };
        $scope.updating = true;
        $scope.userSearchText = "";
        $scope.users = userService.get();
        $scope.ctrl = $scope;
        
        lists.promise
            .then(function () {
                $scope.list = angular.copy($filter('filter')(lists, {id: $routeParams.id})[0]);
                $scope.updating = false;
            });
        
        $scope.updateList = function () {
            $scope.updating = true;
            listService.update($scope.list)
                .then(function (updatedList) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Liste aktualisiert')
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                    $location.path("/lists/" + updatedList.id);
                }).finally(function () {
                    $scope.updating = false;
                });
        };

        $scope.addUserToOwners = function (selectedUser) {
            if(selectedUser){
                $scope.list.owners.push(selectedUser);
            }

            $scope.userSearchText = "";
        };
        
        $scope.removeUserFromOwners = function (index, user){
            if(user.username == $rootScope.user.username){
                $mdDialog.show(
                    $mdDialog.confirm()
                        .title("Warnung!")
                        .content('Wenn du dich selbst aus der Liste der berechtigten Personen entfernst, kannst du dich nach dem Aktualisieren nicht selbst wieder hinzufügen. Dennoch fortfahren?')
                        .ok('Ja')
                        .cancel('Nein')
                ).then(function(){
                        $scope.list.owners.splice(index, 1);
                    })
            }else {
                $scope.list.owners.splice(index, 1);
            }
        };

        $scope.hasProperty = function (user) {
            var filter = $scope.userSearchText;
            var concatenatedFirstAndLastName = null;
            if(user.firstName && user.lastName){
                concatenatedFirstAndLastName = user.firstName + " " + user.lastName;
            }
            return user.username.toUpperCase().indexOf(filter.toUpperCase()) >= 0 ||
                (user.firstName && user.firstName.toUpperCase().indexOf(filter.toUpperCase()) >= 0) ||
                (user.lastName && user.lastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0)||
                (concatenatedFirstAndLastName && concatenatedFirstAndLastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0);
        };

        $scope.notContained = function (user){
            for(var i = 0; i < $scope.list.owners.length; i++){
                if(user.username == $scope.list.owners[i].username){
                    return false;
                }
            }

            return true;
        };

        var fetchUsersIfNecessary = function(){
            if(!$scope.users.loaded){
                userService.fetch();
            }
        };

        fetchUsersIfNecessary();

        $scope.disableUpdateButton = function(createShoppingListForm){
            return !createShoppingListForm || !createShoppingListForm.$valid || createShoppingListForm.$pristine || $scope.list.owners.length == 0;
        };
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/lists/:id/edit', {
        templateUrl: '/app/lists/edit/editList.html',
        controller: 'editList'
    });
}]);