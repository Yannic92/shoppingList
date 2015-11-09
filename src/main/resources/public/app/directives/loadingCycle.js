var loadingCycle = function() {
    return {
        templateUrl: 'app/directives/loadingCycle.html',
        replace: true
    };
};

shoppingList.directive("loadingCycle", loadingCycle);