export default class LoginController {

    constructor($rootScope, $scope, authService, $mdToast, $mdMedia, userService, $routeParams, navigationService) {

        this.$rootScope = $rootScope;
        this.$routeParams = $routeParams;
        this.authService = authService;
        this.userService = userService;
        this.navigationService = navigationService;
        this.$mdToast = $mdToast;
        this.$mdMedia = $mdMedia;

        this.credentials = this.userService.getCredentials();
        this.loggingIn = false;
        this.$rootScope.loading = true;
        this.$rootScope.title = 'Login';

        this._init();
        this._initDestroyListener($scope);
    }

    loginDisabled() {
        return (this.loginForm && !this.loginForm.$valid) || this.loggingIn;
    }

    login() {
        this._loggingIn();
        this.authService.authenticate(this.credentials)
            .then((user) => {
                this.$rootScope.user = user;
                if (this.credentials.rememberMe) {
                    this.userService.storeCredentials(this.credentials);
                }
                this._showWelcomeToast();
                this.navigationService.goto('/lists', true);
            }, (error) => {
                if (error.status == 401) {
                    this.$rootScope.error = true;
                    this.$rootScope.errorMessage = 'Zugangsdaten nicht korrekt';
                    this.navigationService.goToTopOfThePage();
                }
            }).finally(() => {
                this._loggingInFinished();
            });
    }

    gtSm() {
        return this.$mdMedia('gt-sm');
    }

    _init() {
        if (this.$routeParams.historyRoot) {
            this.$rootScope.usersHistoryLength = this.$routeParams.historyRoot;
        }

        if (this.authService.loggedOut) {
            this.navigationService.reload();
        }

        if (!this.$rootScope.authenticationAlreadyChecked) {
            this._redirectIfLoggedIn();
        } else {
            this.$rootScope.loading = false;
        }
    }

    _redirectIfLoggedIn() {
        this._loggingIn();
        this.authService.isAuthenticated()
            .then((user) => {
                this.$rootScope.user = user;
                this.navigationService.goto('/lists', true);
            }).finally(() => {
                this._loggingInFinished();
            });
    }

    _showWelcomeToast() {
        var name = this.$rootScope.user.firstName ? this.$rootScope.user.firstName : this.$rootScope.user.username;
        var message = 'Hallo ' + name;
        this.$mdToast.show(
            this.$mdToast.simple()
                .content(message)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    _loggingIn() {
        this.loggingIn = true;
        this.$rootScope.loading = true;
        this.$mdToast.show(
            this.$mdToast.simple()
                .content('Prüfe Authentifizierung...')
                .position('bottom right')
                .hideDelay(0)
        );
    }

    _loggingInFinished() {
        this.loggingIn = false;
        this.$rootScope.loading = false;
        this.$mdToast.hide();
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {

            this.$rootScope.reset();
        });
    }
}