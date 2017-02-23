export default class AuthService {

    /*@ngInject*/
    constructor($http, $rootScope, userService, credentialService) {

        this.$http = $http;
        this.$rootScope = $rootScope;
        this.userService = userService;
        this.credentialService = credentialService;

        this.loggedOut = false;
        this.loggingIn = false;
    }

    authenticate(credentials) {
        this.loggingIn = true;

        return this.credentialService.storeCredentials(credentials)
            .then(
                () => this.userService.getCurrentUser())
            .then(
                user => this._handleSuccessfulLogin(user),
                error => this._handleErrorLogin(error)
            )
            .catch(
                error => this._handleErrorLogin(error)
            );
    }

    isAuthenticated() {
        this.loggingIn = true;
        this.$rootScope.authenticationAlreadyChecked = true;
        return this.credentialService.getCredentials()
            .then(credentials => {
                return this.authenticate(credentials);
            })
            .catch((error) => {
                this.loggingIn = false;
                return Promise.reject(error);
            });
    }

    logout() {
        return this.$http.post('/logout', {})
            .then(() => this._handleLogoutFinished())
            .catch(() => this._handleLogoutFinished());
    }

    _handleLogoutFinished() {
        this.$rootScope.authenticated = false;
        this.$rootScope.headers = {};
        this.$rootScope.user = null;
        this.credentialService.clearCredentials();
        this.loggedOut = true;
    }

    _handleSuccessfulLogin(currentUser) {

        this.$rootScope.authenticated = true;
        this.$rootScope.user = currentUser;
        this.loggingIn = false;

        return Promise.resolve(currentUser);
    }

    _handleErrorLogin(error) {

        return this.credentialService.clearCredentials()
            .then(() => {
                return Promise.reject(error);
            })
            .catch(() => {
                return Promise.reject(error);
            });
    }
}