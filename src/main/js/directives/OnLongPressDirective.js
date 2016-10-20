export default function OnLongPressDirective($timeout) {
    return {
        restrict: 'A',
        link: function($scope, $elm, $attrs) {
            $elm.bind('touchstart', function() {
                $scope.longPressFinished = false;

                $scope.longPressTimeout = $timeout(function() {
                    $scope.$apply(function() {
                        $scope.$eval($attrs.onLongPress);
                    });
                    $scope.longPressFinished = true;
                }, 600);
            });

            $elm.bind('touchmove', function(){
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
                        $scope.$eval($attrs.onTouchEnd);
                    });
                }

                if($scope.longPressFinished) {
                    evt.stopPropagation();
                }
            });
        }
    };
}