import angular from 'angular';
export default class UserDataController {

    /*@ngInject*/
    constructor($rootScope, userService, authService, $mdToast) {

        this.$rootScope = $rootScope;
        this.$rootScope.title = 'Benutzerdaten';
        this.user = angular.copy($rootScope.user);
        this.userService = userService;
        this.authService = authService;
        this.$mdToast = $mdToast;
        this.updating = false;
    }

    updateUserData() {
        this.updating = true;
        this.userService.update(this.user)
            .then(this._signInWIthNewCredentials)
            .finally(this._finishUpdating);
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
        this.updating = false;
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