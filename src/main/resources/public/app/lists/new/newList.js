shoppingList.controller('newList', ['$scope','$rootScope','listService','$mdToast','userService',
    function ($scope, $rootScope, listService,$mdToast, userService) {

        $rootScope.title = "Neue Einkaufsliste";
        $rootScope.loading = false;
        $scope.userSearchText = "";
        $scope.ctrl = $scope;
        $scope.list = {
            name: "",
            owners: [$rootScope.user]
        };

        $scope.users = userService.get();

        $scope.hasProperty = function (user, value,list) {
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

        $scope.firstNameOrLastNameIsDefined = function(user){

            return user.firstName || user.lastName;
        };

        $scope.createList = function () {
            $rootScope.loading = true;
            $rootScope.errorMessage = '';
            $rootScope.error = false;

            listService.create($scope.list)
                .then(function (createdList) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Neue Liste erstellt')
                            .position("bottom right")
                            .hideDelay(3000)
                    );
                    $scope.$parent.goto("/lists/" +  createdList.entityId, true);
                }, function(error){
                    $rootScope.errorMessage = error.data.message;
                    $rootScope.error = true;
                })
                .finally(function () {
                    $rootScope.loading = false;
                });
        };

        $scope.addUserToOwners = function (selectedUser) {
            if(selectedUser){
                $scope.list.owners.push(selectedUser);
            }

            $scope.userSearchText = "";
        };

        $scope.removeUserFromOwners = function (index, user){
            $scope.list.owners.splice(index, 1);
        };

        $scope.isLoggedInUser = function (user) {
            return user && user.username === $rootScope.user.username;
        };

        $scope.$on('$destroy', function(){
            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/newList', {
        templateUrl: '/app/lists/new/newList.html',
        controller: 'newList'
    });
}]);