export default class DeleteUserController {

    /*@ngInject*/
    constructor($rootScope, userService, authService, navigationService) {

        $rootScope.title = 'Konto löschen';

        this.userService = userService;
        this.authService = authService;
        this.navigationService = navigationService;
        this.user = $rootScope.user;
    }

    deleteAccount() {
        this.userService.deleteUser(this.user)
            .then(() => this._logout());
    }

    _logout() {
        this.authService.logout()
            .finally(() => this.navigationService.goto('/login'));
    }
}