shoppingList.controller('dictionary',[ '$rootScope', '$scope', 'articleService',
    function ($rootScope, $scope, articleService) {

        $rootScope.title="Wörterbuch";

        $scope.articles = articleService.get();

        $scope.deleteArticle = function (article) {

            articleService.delete(article)
                .then(function(){

                }, function(error){
                    $rootScope.error = true;
                    $rootScope.errorMessage = error.data.message;
                });
        };

        $scope.dictionaryIsEmpty = function () {
            return !$scope.articles || !$scope.articles.length || $scope.articles.length == 0;
        };

        $scope.$on('$destroy', function(){

            $rootScope.reset();
        });
    }
]);

shoppingList.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/dictionary', {
        templateUrl: '/app/article/dictionary/dictionary.html',
        controller: 'dictionary'
    });
}]);
