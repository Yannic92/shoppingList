shoppingList.directive('onLongPress', function($timeout) {
    return {
        restrict: 'A',
        link: function($scope, $elm, $attrs) {
            $elm.bind('touchstart', function(evt) {
                $scope.longPressFinished = false;

                $scope.longPressTimeout = $timeout(function() {
                    $scope.$apply(function() {
                        $scope.$eval($attrs.onLongPress)
                    });
                    $scope.longPressFinished = true;
                }, 600);
            });

            $elm.bind('touchmove', function(evt){
                if($scope.longPressTimeout) {
                    $timeout.cancel($scope.longPressTimeout);
                }
            });

            $elm.bind('touchend', function(evt) {

                if($scope.longPressTimeout) {
                    $timeout.cancel($scope.longPressTimeout);
                }

                if ($attrs.onTouchEnd) {
                    $scope.$apply(function() {
                        $scope.$eval($attrs.onTouchEnd)
                    });
                }

                if($scope.longPressFinished) {
                    evt.stopPropagation();
                }
            });
        }
    };
});