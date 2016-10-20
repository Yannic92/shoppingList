import angular from 'angular';
import HttpInterceptorBase from './HttpInterceptorBase';
export default class HttpInterceptor extends HttpInterceptorBase{
    constructor($location, $q, $injector, $rootScope) {


        super();
        this.$rootScope = $rootScope;
        this.$injector = $injector;
        this.$location = $location;
        this.$q = $q;
        this.sessionTimeOutCheck = false;
        this.connectionLossNotification = false;

        this.sessionTimeoutCheckPromise = {};

    }

    responseError(rejection) {
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
        else if (rejection.status == 401 || (rejection.status == 403 && rejection.data.message.indexOf('CSRF') > -1)) {

            if (this.sessionTimeOutCheck && !(rejection.config.url == 'sLUsers/current')) {
                return this.sessionTimeoutCheckPromise
                    .then(() => {
                        var $http = this.$injector.get('$http');
                        return $http(rejection.config);
                    }, () => {
                        return this._handleSessionTimeout(rejection);
                    });
            } else if (this.$rootScope.authenticated && !this.sessionTimeOutCheck) {
                this.sessionTimeoutCheckPromise = this._checkForSessionTimeout(rejection);
                return this.sessionTimeoutCheckPromise
                    .then(() => {
                        var $http = this.$injector.get('$http');
                        return $http(rejection.config);
                    }, () => {
                        return this._handleSessionTimeout(rejection);
                    });
            } else if (!this.$injector.get('authService').loggingIn) {
                this.$location.path('/login').replace();
            }
        } else if (rejection.status == 400 || rejection.status == 404) {
            this.$rootScope.error = true;
            this.$rootScope.errorMessage = rejection.data.message;
            this.$rootScope.goToTop();
        } else {
            this.$rootScope.error = true;
            this.$rootScope.errorMessage = 'Sorry! Etwas ging schief. Bitte versuche es später erneut';
            this.$rootScope.goToTop();
        }
        return this.$q.reject(rejection);
    }

    _handleSessionTimeout(rejection) {
        this.$rootScope.authenticated = true;
        var $mdDialog = this.$injector.get('$mdDialog');
        return $mdDialog.show(
            $mdDialog.alert()
                .parent(angular.element(document.querySelector('#popupContainer')))
                .clickOutsideToClose(true)
                .title('Sitzung abgelaufen')
                .content('Ihre Sitzung ist abgelaufen. Bitte melden Sie sich erneut an.')
                .ariaLabel('Sitzung abgelaufen')
                .ok('OK')
        ).then(() => {
            this.$injector.get('authService').logout();
        }).then(() => {
            this.$location.path('/login');
        }).finally(() => {
            this.$rootScope.error = true;
            this.$rootScope.errorMessage = 'Verbindung fehlgeschlagen';
            this.$rootScope.goToTop();
            return this.$q.reject(rejection);
        });
    }

    _handleOfflineRequest() {
        var $mdDialog = this.$injector.get('$mdDialog');
        return $mdDialog.show(
            $mdDialog.confirm()
                .title('Verbindung fehlgeschlagen')
                .content('Entweder besteht aktuell keine Verbindung zum Internet, oder der Dient wird gewartet. Möchtest du es erneut versuchen?')
                .ok('Ja')
                .cancel('Nein')
        ).then(() => {
            this.connectionLossNotification = false;
        });
    }

    _checkForSessionTimeout() {
        this.sessionTimeOutCheck = true;
        return this.$injector.get('authService').isAuthenticated();
    }
}