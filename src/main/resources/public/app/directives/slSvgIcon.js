var slSvgIcon = function() {
    return {
        templateUrl: 'app/directives/slSvgIcon.html',
        scope: {
            size: '@',
            src: '@'
        },
        replace: true
    };
};

shoppingList.directive("slSvgIcon", slSvgIcon);