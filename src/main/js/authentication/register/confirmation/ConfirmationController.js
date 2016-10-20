export default class ConfirmationController {

    constructor($scope, $rootScope, userService, $routeParams) {

        this.$rootScope = $rootScope;
        this.userService = userService;
        this.username = $routeParams.username;
        this.confirmation = {code: $routeParams.code ? $routeParams.code : ''};

        this._initDestroyListener($scope);
        this._confirm();
    }

    _confirm() {
        this.userService.confirmRegistrationFor(this.username, this.confirmation)
            .then(() => {
                this.success = true;
            }, () => {
                this.success = false;
            }).finally(() => {
                this.$rootScope.loading = false;
            });
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {

            this.$rootScope.reset();
        });
    }
}