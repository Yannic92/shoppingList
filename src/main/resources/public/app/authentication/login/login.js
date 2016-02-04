shoppingList.controller('login',[ '$rootScope', '$scope', '$location', 'authService','$mdToast','$mdMedia','userService','$window', '$routeParams',
    function ($rootScope, $scope, $location, authService, $mdToast,$mdMedia, userService, $window, $routeParams) {

        $scope.credentials = userService.getCredentials();
        $scope.loggingIn = false;
        $rootScope.loading = true;
        $rootScope.title="Login";

        var init = function() {
            if ($routeParams.historyRoot) {
                $rootScope.usersHistoryLength = $routeParams.historyRoot;
            }

            if (authService.loggedOut) {
                $window.location.reload();
            }

            if (!$rootScope.authenticationAlreadyChecked) {
                redirectIfLoggedIn();
            }else{
                $rootScope.loading = false;
            }
        };

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
                    if(error.status == 401){
                        $rootScope.error = true;
                        $rootScope.errorMessage = "Zugangsdaten nicht korrekt";
                        $rootScope.goToTop();
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

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });

        $scope.gtSm = function () {
            return $mdMedia('gt-sm');
        };

        init();
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
