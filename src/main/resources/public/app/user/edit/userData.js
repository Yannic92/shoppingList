shoppingList.controller('userData', ['$rootScope', '$scope', 'userService', 'authService','$mdToast',
    function ($rootScope, $scope, userService, authService,$mdToast) {
        $rootScope.title = "Benutzerdaten";
        $scope.updating = false;
        $scope.user = angular.copy($rootScope.user);
        
        $scope.updateUserData = function () {
            $scope.updating = true;
            userService.update($scope.user)
                .then(function(){
                    var credentials = {
                        username : $scope.user.username,
                        password : $scope.user.password
                    };
                  
                    authService.authenticate(credentials)
                        .then(function (user) {
                            $rootScope.user = user;
                            $mdToast.show(
                                $mdToast.simple()
                                    .content('Benutzerdaten wurden aktualisiert!')
                                    .position("bottom right")
                                    .hideDelay(3000)
                            );
                        });
                }).finally(function () {
                    $scope.updating = false;
                });
        }
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/userData', {
        templateUrl: '/app/user/edit/userData.html',
        controller: 'userData'
    });
}]);