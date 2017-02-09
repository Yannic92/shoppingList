export default class UserDataController {

    /*@ngInject*/
    constructor($rootScope, userService, authService, $mdToast) {

        this.$rootScope = $rootScope;
        this.$rootScope.title = 'Benutzerdaten';
        this.user = $rootScope.user.copy();
        this.userService = userService;
        this.authService = authService;
        this.$mdToast = $mdToast;
        this.updating = false;
    }

    updateUserData() {
        this.$rootScope.loading = true;
        this.userService.updateUser(this.user)
            .then(() => this._signInWIthNewCredentials())
            .finally(() => this._finishUpdating());
    }

    formIsReadyToUpdate() {
        return this.updateUserDataForm.$valid && !this.updateUserDataForm.$pristine;
    }

    fieldWasTouched(fieldName) {
        return this.updateUserDataForm[fieldName] && this.updateUserDataForm[fieldName].$touched;
    }

    fieldHasError(fieldName) {
        return this.updateUserDataForm[fieldName] && this.updateUserDataForm[fieldName].$error;
    }

    _signInWIthNewCredentials() {
        var credentials = {
            username: this.user.username,
            password: this.user.password
        };

        this.authService.authenticate(credentials)
            .then((user) => {
                this.$rootScope.user = user;
                this._showUpdateFinishedToast();
            });
    }

    _finishUpdating() {
        this.$rootScope.loading = true;
        this._showUpdateFinishedToast();
    }

    _showUpdateFinishedToast() {
        this.$mdToast.show(
            this.$mdToast.simple()
                .content('Benutzerdaten wurden aktualisiert!')
                .position('bottom right')
                .hideDelay(3000)
        );
    }
}