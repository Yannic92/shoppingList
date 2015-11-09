shoppingList.controller('login',[ '$rootScope', '$scope', '$location', 'authService','$mdToast',
    function ($rootScope, $scope, $location, authService, $mdToast) {
        
        
        $scope.credentials = {};
        $scope.loggingIn = false;
        $rootScope.title="Login";

        $scope.login = function () {
            $scope.loggingIn = true;
            authService.authenticate($scope.credentials)
                .then(function (user) {
                    $rootScope.user = user;
                    showWelcomeToast();
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
        
        var showWelcomeToast = function () {
            var name = $rootScope.user.firstName ? $rootScope.user.firstName : $rootScope.user.username;
            var message = "Hallo " + name;
            $mdToast.show(
                $mdToast.simple()
                    .content(message)
                    .position("bottom right")
                    .hideDelay(3000)
            );
        };
        
        $scope.loginDisabled = function () {
            return $scope.loggingIn || ($scope.loginForm && !$scope.loginForm.$valid);
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
