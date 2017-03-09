import HttpInterceptor from './HttpInterceptor';
import BasicAuthUtils from './BasicAuthUtils';

export default class AuthenticationInterceptor extends HttpInterceptor{

    /*@ngInject*/
    constructor($q, $injector, $rootScope, navigationService, credentialService) {


        super();
        this.$rootScope = $rootScope;
        this.$injector = $injector;
        this.navigationService = navigationService;
        this.credentialService = credentialService;
        this.$q = $q;
        this.connectionLossNotification = false;
    }

    request(config) {
        return this.credentialService.getCredentials()
            .then(
                credentials => {
                    return this._addAuthorizationHeader(config, credentials);
                },
                () => {
                    return config;
                }
            );
    }

    _addAuthorizationHeader(config, credentials) {
        config.headers['Authorization'] = BasicAuthUtils.buildAuthorizationHeader(credentials.username, credentials.password);
        return config;
    }

    responseError(rejection) {
        if (rejection.status <= 0 || rejection.status == 502 && rejection.config.method === 'GET') {
            this._notifyAboutOfflineState();
            return this.$q.reject(rejection);
        }
        if ((rejection.status <= 0 || rejection.status == 502) && !this.connectionLossNotification) {
            this.connectionLossNotification = true;
            this.connectionLossPromise = this._handleOfflineRequest(rejection);
            this.connectionLossPromise
                .then(() => {
                    var $http = this.$injector.get('$http');
                    this.connectionLossNotification = false;
                    return $http(rejection.config);
                }, () => {
                    this.connectionLossNotification = false;
                    return this.$q.reject(rejection);
                });
            return this.connectionLossPromise;
        } else if ((rejection.status <= 0 || rejection.status == 502) && this.connectionLossNotification) {
            return this.connectionLossPromise
                .then(() => {
                    var $http = this.$injector.get('$http');
                    return $http(rejection.config);
                }, () => {
                    return this.$q.reject(rejection);
                });
        }
        else if (rejection.status == 401) {
            // Do nothing
        } else if (rejection.status == 400 || rejection.status == 404) {
            this.$rootScope.error = true;
            this.$rootScope.errorMessage = rejection.data.message;
            this.navigationService.goToTopOfThePage();
        } else {
            this.$rootScope.error = true;
            this.$rootScope.errorMessage = 'Sorry! Etwas ging schief. Bitte versuche es später erneut';
            this.navigationService.goToTopOfThePage();
        }
        return this.$q.reject(rejection);
    }

    _notifyAboutOfflineState() {
        var $mdToast = this.$injector.get('$mdToast');
        $mdToast.show ($mdToast.simple()
            .content('Keine aktive Verbindung')
            .position('bottom right')
            .hideDelay(3000));
    }

    _handleOfflineRequest() {
        var $mdDialog = this.$injector.get('$mdDialog');
        return $mdDialog.show(
            $mdDialog.confirm()
                .title('Verbindung fehlgeschlagen')
                .content('Entweder besteht aktuell keine Verbindung zum Internet, oder der Dienst wird gewartet. Möchtest du es erneut versuchen?')
                .ok('Ja')
                .cancel('Nein')
        ).then(() => {
            this.connectionLossNotification = false;
        });
    }
}