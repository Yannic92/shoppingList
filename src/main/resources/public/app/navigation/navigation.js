shoppingList.controller('navigation', ['$rootScope', '$scope', '$location', 'authService', '$route', '$mdComponentRegistry','$mdMedia','$window',
    function ($rootScope, $scope, $location, authService, $route, $mdComponentRegistry,$mdMedia, $window) {
        $scope.lastPath = "";

        $scope.openNav = function(){
            $mdComponentRegistry.when('leftNav').then(function(it){
                it.open();
            });
        };

        $scope.toggleNav = function(){

            $mdComponentRegistry.when('leftNav').then(function(it){
                it.toggle();
            });
        };

        $scope.closeNav = function(){
            if(!$mdMedia('gt-sm')) {
                $mdComponentRegistry.when('leftNav').then(function(it){
                    it.close();
                });
            }
        };

        var urlIsDefined = function(url){
            return url && url.$$route;
        };

        var urlMatchesPath = function(url, path){
            return urlIsDefined(url) && url.$$route.originalPath == path;
        };

        var urlStartsWithPath = function(url, path){
            return urlIsDefined(url) && url.$$route.originalPath.indexOf(path) == 0;
        };

        var isFreeRoute = function (url) {
            return urlMatchesPath(url, '/login') || urlMatchesPath(url, '/logout') || urlStartsWithPath(url, '/register');
        };

        var redirectToLoginIfNotFreeRotue = function(newUrl) {
            if (!isFreeRoute(newUrl)) {
                if(urlIsDefined(newUrl)){
                    $scope.lastPath = newUrl;
                }
                $location.replace();
                $location.path('/login')
            }
        };

        var redirectToLoginIfAuthenticationRequired = function(newUrl) {
            authService.isAuthenticated()
                .then(function (user) {
                    $rootScope.user = user;
                    if (!$rootScope.authenticated) {
                        redirectToLoginIfNotFreeRotue(newUrl);
                    }else{
                        $route.reload();
                    }
                });
        };

        $rootScope.$on('$routeChangeStart', function (event, newUrl, oldUrl) {
            if(!$rootScope.authenticated){
                if(!isFreeRoute(newUrl)){
                    event.preventDefault();
                    redirectToLoginIfAuthenticationRequired(newUrl);
                }

            }
        });

        $rootScope.$on('$routeChangeSuccess', function () {
            $scope.closeNav();
            $rootScope.errorMessage = "";
            $rootScope.error = false;
            $rootScope.goToTop();
        });

        $rootScope.$watch('error', function(){
            if($rootScope.error){
                $rootScope.goToTop()
            }
        });

        $scope.goto = function(path){

            $scope.closeNav();
            $location.path(path);
        };

        $scope.logout = function () {
            authService.logout()
                .then(function () {
                    $location.path("/login");
                    $window.location.reload();
                });
        };

        $scope.closeError = function(){
            $rootScope.error = false;
            $rootScope.errorMessage = "";
        };
    }
]);

