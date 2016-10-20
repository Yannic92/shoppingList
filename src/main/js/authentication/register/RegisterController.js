export default class RegisterController {

    constructor($scope, $rootScope, userService, $location) {

        this.$rootScope = $rootScope;
        this.userService = userService;
        this.$location = $location;

        this.$rootScope.title = 'Registrieren';
        this.$rootScope.loading = false;

        this.user = {authorities: [{authority: 'USER'}]};

        this._initDestroyListener($scope);
    }

    register() {
        this.$rootScope.loading = true;
        this.userService.create(this.user)
            .then(() => {
                this.$location.path('/register/confirmation');
            })
            .finally(() => {
                this.$rootScope.loading = false;
            });
    }

    submitIsDisabled() {
        return !this.registerForm.$valid;
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {

            this.$rootScope.reset();
        });
    }
}