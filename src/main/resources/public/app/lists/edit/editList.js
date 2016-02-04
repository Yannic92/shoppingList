shoppingList.controller('editList', ['$scope','$rootScope','listService','$filter','$routeParams','$mdToast','$location','$mdDialog','userService','$timeout',
    function ($scope, $rootScope, listService,$filter,$routeParams,$mdToast,$location,$mdDialog,userService,$timeout) {
        $rootScope.title = "Einkaufsliste bearbeiten";
        var lists = listService.get();
        $scope.list = {
            name: ''
        };
        $rootScope.loading = true;
        $scope.userSearchText = "";
        $scope.users = userService.get();
        $scope.ctrl = $scope;
        $scope.saveIsVisible = false;

        lists.promise
            .then(function () {
                $scope.list = angular.copy($filter('filter')(lists, {entityId: $routeParams.id})[0]);
                $rootScope.loading = false;
            });

        $scope.updateList = function () {
            $rootScope.loading = true;
            return listService.update($scope.list)
                .then(function (updatedList) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Liste aktualisiert')
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                }).finally(function () {
                    $scope.hideSave();
                    $rootScope.loading = false;
                    $scope.updateShoppingListForm.$setPristine();
                });
        };

        $scope.addUserToOwners = function (selectedUser) {
            if(selectedUser){
                $scope.list.owners.push(selectedUser);
                $scope.updateList()
                    .then(function(){
                        $scope.userSearchText = "";
                    });
            }
        };

        $scope.removeUserFromOwners = function (index, user){
            if(isLoggedInUser(user)){
                $mdDialog.show(
                    $mdDialog.confirm()
                        .title("Warnung!")
                        .content('Wenn du dich selbst aus der Liste der berechtigten Personen entfernst, kannst du dich nach dem Aktualisieren nicht selbst wieder hinzufügen. Dennoch fortfahren?')
                        .ok('Ja')
                        .cancel('Nein')
                ).then(function(){
                    $scope.list.owners.splice(index, 1);
                    $scope.updateList();
                })
            }else {
                $scope.list.owners.splice(index, 1);
                $scope.updateList();
            }
        };

        var isLoggedInUser = function (user) {
            return user && user.username === $rootScope.user.username;
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

        $scope.nameChanged = function(createShoppingListForm){
            return createShoppingListForm && createShoppingListForm.$valid && !createShoppingListForm.$pristine;
        };

        $scope.firstNameOrLastNameIsDefined = function(user){

            return user.firstName || user.lastName;
        };

        $scope.showSave = function(){
            $scope.saveIsVisible = true;
        };

        $scope.hideSave = function(){
            $timeout(function(){
                $scope.saveIsVisible = false;
            }, 100);

        };

        $scope.$on('$destroy', function(){
            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/lists/:id/edit', {
        templateUrl: '/app/lists/edit/editList.html',
        controller: 'editList'
    });
}]);