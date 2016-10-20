export default class DeleteUserController {

    constructor($rootScope, userService, authService, $location) {

        this.userService = userService;
        this.authService = authService;
        this.$location = $location;
        this.$rootScope = $rootScope;
        this.$rootScope.title = 'Konto löschen';
    }

    deleteAccount() {
        this.userService
            .delete(this.$rootScope.user)
            .then(() => {
                this._logout();
            });
    }

    _logout() {
        this.authService.logout()
            .finally(() => {
                this.$location.path('/login');
            });
    }
}