shoppingList.controller('dictionary',[ '$rootScope', '$scope', 'articleService', '$mdDialog','$mdToast',
    function ($rootScope, $scope, articleService, $mdDialog, $mdToast) {

        $scope.articles = articleService.get();

        $scope.dictionaryIsEmpty = function () {
            return !$scope.articles || !$scope.articles.length || $scope.articles.length == 0;
        };

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });

        $scope.clearDictionary = function(ev){

            var lengthBefore = $scope.articles.length;

            var confirm = $mdDialog.confirm()
                .title("Möchtest du dein Wörterbuch wirklich leeren?")
                .content("Alle ungenutzten Einträge werden unwideruflich gelöscht.")
                .targetEvent(ev)
                .ok('Ja')
                .cancel('Nein');

            return $mdDialog.show(confirm)
                .then(function () {
                    $rootScope.loading = true;
                    return articleService.deleteUnused();
                }).then(function(){
                    if(lengthBefore != $scope.articles.length) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content("Ungenutzte Einträge gelöscht")
                                .position("bottom right")
                                .hideDelay(3000)
                        );
                    }else{
                        $mdToast.show(
                            $mdToast.simple()
                                .content("Alle Einträge sind in Verwendung")
                                .position("bottom right")
                                .hideDelay(3000)
                        );
                    }
                }).finally(function(){
                    $rootScope.loading = false;
                });
        };

        var init = function () {

            $rootScope.title = "Wörterbuch";
            $rootScope.options = [
                {
                    icon: "/img/icons/communication/ic_clear_all_24px.svg",
                    text: "Wörterbuch leeren",
                    action: $scope.clearDictionary,
                    disabled: $scope.dictionaryIsEmpty
                }
            ];
        };

        init();
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/dictionary', {
        templateUrl: '/app/article/dictionary/dictionary.html',
        controller: 'dictionary'
    });
}]);
