export default class DeleteUserController {

    /*@ngInject*/
    constructor($rootScope, userService, authService, navigationService) {

        this.userService = userService;
        this.authService = authService;
        this.navigationService = navigationService;
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
                this.navigationService.goto('/login');
            });
    }
}