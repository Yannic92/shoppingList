shoppingList.controller('confirmationNotification',[ '$scope', '$rootScope',
    function ($scope, $rootScope) {

        $rootScope.title="Registrierung bestätigen";
        $rootScope.loading = false;
        
        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    var routeConfig = {
        templateUrl: '/app/authentication/register/confirmation/confirmationNotification.html',
        controller: 'confirmationNotification'
    };

    $routeProvider.when('/register/confirmation', routeConfig);
}]);
