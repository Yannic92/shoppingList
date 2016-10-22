export default class AuthService {

    /*@ngInject*/
    constructor($http, $rootScope, userService) {
        this.$http = $http;
        this.$rootScope = $rootScope;
        this.userService = userService;

        this.loggedOut = false;
        this.loggingIn = false;

        this.userEndpoint = 'sLUsers/current';
    }

    authenticate(credentials) {
        this.loggingIn = true;
        var headers = this._getAuthenticationHeader(credentials);
        this.$rootScope.authenticationAlreadyChecked = true;
        return this.$http.get(this.userEndpoint, {
            headers: headers
        }).then(() => {
            return this.$http.get(this.userEndpoint)
                .then((response) => this._handleSuccessfulLogin(response))
                .finally(() => {
                    this.loggingIn = false;
                });
        });
    }

    isAuthenticated() {
        this.loggingIn = true;
        this.$rootScope.authenticationAlreadyChecked = true;
        var credentials = this.userService.getCredentials();
        if (credentials && credentials.username) {
            return this.authenticate(credentials);
        }

        return this.$http.get(this.userEndpoint)
            .then((response) => this._handleSuccessfulLogin(response))
            .finally(() => {
                this.loggingIn = false;
            });
    }

    _getAuthenticationHeader(credentials) {
        return credentials ? {
            authorization: 'Basic '
            + btoa(credentials.username + ':'
                + credentials.password)
        } : {};
    }

    logout() {
        return this.$http.post('/logout', {})
            .finally(() => {
                this.$rootScope.authenticated = false;
                this.$rootScope.headers = {};
                this.$rootScope.user = null;
                this.userService.clearCredentials();
                this.loggedOut = true;
            });
    }

    _handleSuccessfulLogin(response) {
        if (response.data && response.data.username) {
            this.$rootScope.authenticated = true;
            return response.data;
        } else {
            this.$rootScope.authenticated = false;
        }
    }
}