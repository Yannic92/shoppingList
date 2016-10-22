export default class RegisterController {

    /*@ngInject*/
    constructor($scope, $rootScope, userService, navigationService) {

        this.$rootScope = $rootScope;
        this.userService = userService;
        this.navigationService = navigationService;

        this.$rootScope.title = 'Registrieren';
        this.$rootScope.loading = false;

        this.user = {authorities: [{authority: 'USER'}]};

        this._initDestroyListener($scope);
    }

    register() {
        this.$rootScope.loading = true;
        this.userService.create(this.user)
            .then(() => {
                this.navigationService.goto('/register/confirmation');
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