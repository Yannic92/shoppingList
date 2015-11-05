shoppingList.controller('userData', ['$rootScope', '$scope', 'userService', 'authService', '$location',
    function ($rootScope, $scope, userService, authService, $location) {
        $rootScope.title = "Benutzerdaten";
        
        $scope.user = angular.copy($rootScope.user);
        
        $scope.updateUserData = function () {
            userService.update($scope.user)
                .then(function(){
                    var credentials = {
                        username : $scope.user.username,
                        password : $scope.user.password
                    };
                    authService.logout()
                        .then(function () {
                            authService.authenticate(credentials)
                                .then(function (user) {
                                    $rootScope.user = user;
                                    $location.path("/userDataSuccess");
                                });
                        })
                });
        }
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/userData', {
        templateUrl: '/app/user/userData.html',
        controller: 'userData'
    });
}]);