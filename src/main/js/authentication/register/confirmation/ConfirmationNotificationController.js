export default class ConfirmationNotificationController {

    /*@ngInject*/
    constructor($scope, $rootScope) {

        $rootScope.title = 'Registrierung bestätigen';
        $rootScope.loading = false;

        this._initDestroyListener($scope, $rootScope);
    }

    _initDestroyListener($scope, $rootScope) {
        $scope.$on('$destroy', () => {

            $rootScope.reset();
        });
    }
}