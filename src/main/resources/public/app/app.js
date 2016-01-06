var dependecies = ['ngRoute', 'ngResource','ngMaterial', 'ngAnimate', 'ngAria', 'ngMessages'];
if(!shoppingList){
    var shoppingList = angular.module('shoppingList', dependecies);
}else{
    shoppingList.requires.push.apply(shoppingList.requires, dependecies);
}

shoppingList.config(['$routeProvider', '$httpProvider', '$locationProvider',
    function ($routeProvider, $httpProvider, $locationProvider) {
        $routeProvider.otherwise({redirectTo: '/lists'});
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        $httpProvider.interceptors.push('MyHttpInterceptor');
    }
]);

shoppingList.run(['$rootScope', '$location', '$anchorScroll',
    function ($rootScope, $location, $anchorScroll) {
        $rootScope.authenticated = false;
        $rootScope.user = "";
        $rootScope.reset = function(){
            $rootScope.options = [];
            $rootScope.title = "";
            $rootScope.shortCutAction = {
                available: false
            };
            $rootScope.loading = false;
        };
        $rootScope.goToTop = function () {
            $location.hash('top');
            $anchorScroll();
            $location.hash('');

        }
    }
]);