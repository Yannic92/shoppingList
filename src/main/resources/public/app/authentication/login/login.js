shoppingList.controller('login',[ '$rootScope', '$scope', '$location', 'authService','$mdToast','$mdMedia','userService','$window', '$routeParams',
    function ($rootScope, $scope, $location, authService, $mdToast,$mdMedia, userService, $window, $routeParams) {

        $scope.credentials = userService.getCredentials();
        $scope.loggingIn = false;
        $rootScope.loading = false;
        $rootScope.title="Login";

        if($routeParams.historyRoot){
            $rootScope.usersHistoryLength = $routeParams.historyRoot;
        }

        if(authService.loggedOut){
            $window.location.reload();
        }

        if(authService.loggingOut){
            $window.history.go(-1 * ($window.history.length - $rootScope.usersHistoryLength));
        }

        $scope.login = function () {
            loggingIn();
            authService.authenticate($scope.credentials)
                .then(function (user) {
                    $rootScope.user = user;
                    if($scope.credentials.rememberMe){
                        userService.storeCredentials($scope.credentials);
                    }
                    showWelcomeToast();
                    $location.path('/lists').replace();
                }, function(error){
                    $rootScope.error = true;
                    if(error.status == 401){
                        $rootScope.errorMessage = "Zugangsdaten nicht korrekt";
                    }else{
                        $rootScope.errorMessage = "Dienst nicht erreichbar";
                    }
                }).finally(function(){
                    logginInFinished();
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
            loggingIn();
            authService.isAuthenticated()
                .then(function (user) {
                    $rootScope.user = user;
                    $location.path('/lists').replace();
                }).finally(function(){
                    logginInFinished();
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

        var loggingIn = function(){
            $scope.loggingIn = true;
            $rootScope.loading = true;
            $mdToast.show(
                $mdToast.simple()
                    .content("Prüfe Authentifizierung...")
                    .position("bottom right")
                    .hideDelay(0)
            );
        };

        var logginInFinished = function () {
            $scope.loggingIn = false;
            $rootScope.loading = false;
            $mdToast.hide();
        };

        $scope.loginDisabled = function () {
            return $scope.loggingIn || ($scope.loginForm && !$scope.loginForm.$valid);
        };

        if(!$rootScope.authenticationAlreadyChecked) {
            redirectIfLoggedIn();
        }

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });

        $scope.gtSm = function () {
            return $mdMedia('gt-sm')
        }
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: '/app/authentication/login/login.html',
        controller: 'login'
    }).when('/login/:historyRoot', {
        templateUrl: '/app/authentication/login/login.html',
        controller: 'login'
    });
}]);
