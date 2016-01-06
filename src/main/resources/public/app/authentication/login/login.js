shoppingList.controller('login',[ '$rootScope', '$scope', '$location', 'authService','$mdToast','$mdMedia',
    function ($rootScope, $scope, $location, authService, $mdToast,$mdMedia) {


        $rootScope.credentials = {};
        $scope.loggingIn = false;
        $rootScope.loading = false;
        $rootScope.title="Login";

        $scope.login = function () {
            loggingIn();
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
                    $location.replace();
                    $location.path('/lists')
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

        redirectIfLoggedIn();

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
    });
}]);
