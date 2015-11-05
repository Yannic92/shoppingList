shoppingList.controller('login',[ '$rootScope', '$scope', '$location', 'authService',
    function ($rootScope, $scope, $location, authService) {
        
        
        $scope.credentials = {};
        $scope.loggingIn = false;
        $rootScope.title="Login";

        $scope.login = function () {
            $scope.loggingIn = true;
            authService.authenticate($scope.credentials)
                .then(function (username) {
                    $rootScope.user = username;
                    $location.replace();
                    $location.path('/lists')
                }, function(error){
                    $rootScope.error = true;
                    if(error.status == 401){
                        $rootScope.errorMessage = "Zugangsdaten nicht korrekt";
                    }else{
                        $rootScope.errorMessage = "Dienst nicht erreichbar";
                    }
                }).finally(function(){
                    $scope.loggingIn = false;
                });
        };

        $scope.register = function(){
            $modal.open({
                templateUrl: '/app/authentication/newUser/newUser.html',
                controller: 'newUser',
                size: 'lg'
            });
        };

        var redirectIfLoggedIn = function (){
            $scope.loggingIn = true;
            authService.isAuthenticated()
                .then(function (user) {
                    $rootScope.user = user;
                    $location.replace();
                    $location.path('/lists')
                }).finally(function(){
                   $scope.loggingIn = false;
                });
        };
        
        $scope.loginDisabled = function () {
            return $scope.loggingIn || !$scope.loginForm.$valid;
        };

        redirectIfLoggedIn();
        
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: '/app/authentication/login/login.html',
        controller: 'login'
    });
}]);
