export default class NavigationController {

    constructor($rootScope, $scope, $location, authService, $route, $mdComponentRegistry, $mdMedia, $window, $timeout, $mdToast) {
        this.lastPath = '';
        this.newVersionAvailable = false;
        this.initialLoad = true;

        this.$mdComponentRegistry = $mdComponentRegistry;
        this.$mdMedia = $mdMedia;
        this.$location = $location;
        this.$rootScope = $rootScope;
        this.authService = authService;
        this.$route = $route;
        this.$timeout = $timeout;
        this.$mdToast = $mdToast;
        this.$window = $window;
        this.$scope = $scope;

        this._initRouteChangeStartListener();
        this._initRouteChangeSuccessListener();
        this._initErrorListener();
        this._initUpdateReadyListener();
    }

    openNav() {
        this.$mdComponentRegistry.when('leftNav').then(function (it) {
            it.open();
        });
    }

    toggleNav() {

        this.$mdComponentRegistry.when('leftNav').then(function (it) {
            it.toggle();
        });
    }

    closeNav() {
        if (!this.$mdMedia('gt-sm')) {
            this.$mdComponentRegistry.when('leftNav').then(function (it) {
                it.close();
            });
        }
    }

    static _urlIsDefined(url) {
        return url && url.$$route;
    }

    static _urlMatchesPath(url, path) {
        return NavigationController._urlIsDefined(url) && url.$$route.originalPath == path;
    }

    static _urlStartsWithPath(url, path) {
        return NavigationController._urlIsDefined(url) && url.$$route.originalPath.indexOf(path) == 0;
    }

    static _isFreeRoute(url) {
        return NavigationController._urlStartsWithPath(url, '/login') ||
            NavigationController._urlMatchesPath(url, '/logout') ||
            NavigationController._urlStartsWithPath(url, '/register');
    }

    isOnRoot() {
        var currentPath = this.$location.path();

        return currentPath && currentPath == '/lists';
    }

    redirectToLoginIfNotFreeRotue(newUrl) {
        if (!NavigationController._isFreeRoute(newUrl)) {

            if (NavigationController._urlIsDefined(newUrl)) {
                this.lastPath = newUrl;
            }
            this.goto('/login', true);
        }
    }

    redirectToLoginIfAuthenticationRequired(newUrl) {
        this.$rootScope.routeIsLoading = true;
        this.authService.isAuthenticated()
            .then((user) => {
                this.$rootScope.user = user;
                this.$route.reload();

            }, () => {
                this.redirectToLoginIfNotFreeRotue(newUrl);
            })
            .finally(() => {
                this.$rootScope.routeIsLoading = false;
            });
    }

    handleFinishedHistoryBackChain() {
        this.historyBackTimeout = this.$timeout(() => {
            this.processingHistoryBackChain = false;
            this.routeIsLoading = false;
            this.$rootScope.loading = false;
            this.goto('/login', true);
            this.$mdToast.show(
                this.$mdToast.simple()
                    .content('Zum Beenden erneut tippen')
                    .position('bottom right')
                    .hideDelay(3000)
            );
        }, 500);
    }

    _initRouteChangeStartListener() {
        this.$rootScope.$on('$routeChangeStart', (event, newUrl) => {
            if (this.processingHistoryBackChain) {

                this.$timeout.cancel(this.historyBackTimeout);
                this.$timeout(() => {
                    this.back();
                }).then(() => this.handleFinishedHistoryBackChain());

            } else if (!this.$rootScope.authenticated) {
                this.routeIsLoading = true;
                if (!NavigationController._isFreeRoute(newUrl)) {
                    if (!this.initialLoad) {
                        this.processingHistoryBackChain = true;
                        this.$timeout(() => {
                            this.back();
                        }).then(() => this.handleFinishedHistoryBackChain());
                    } else {
                        event.preventDefault();
                        this.redirectToLoginIfAuthenticationRequired(newUrl);
                    }
                }
            }
        });
    }

    _initRouteChangeSuccessListener() {
        this.$rootScope.$on('$routeChangeSuccess', () => {

            if (!this.processingHistoryBackChain) {
                this.routeIsLoading = false;
            }
            if (this.initialLoad) {
                this.initialLoad = false;
            }

            this.closeNav();
            this.$rootScope.errorMessage = '';
            this.$rootScope.error = false;
            this.$rootScope.goToTop();

            const isRoot = this.isOnRoot();
            this.toggleButtonActive = isRoot && this.$rootScope.authenticated;
            this.backButtonActive = !isRoot && this.$rootScope.authenticated;
        });
    }

    _initErrorListener() {
        this.$rootScope.$watch('error', () => {
            if (this.$rootScope.error) {
                this.$rootScope.goToTop();
            }
        });
    }

    _initUpdateReadyListener() {
        if (window.applicationCache) {
            window.applicationCache.addEventListener('updateready', () => {
                this.newVersionAvailable = true;
                this.$scope.$apply();
            });
        }
    }

    goto(path, replace) {

        this.closeNav();
        this.$location.path(path);
        if (replace) {
            this.$location.replace();
        }
    }

    gotoExternal(path) {
        this.$window.location.href = path;
    }

    logout() {
        this.authService.logout()
            .then(() => {
                this.goto('/login');
            });
    }

    closeError() {
        this.$rootScope.error = false;
        this.$rootScope.errorMessage = '';
    }


    closeUpdateNotification() {
        this.newVersionAvailable = false;
    }

    reload() {
        this.$window.location.reload();
    }

    back() {
        this.$window.history.back();
    }

    isXs() {
        return this.$mdMedia('xs');
    }

    optionsAvailable() {
        return this.$rootScope.options && this.$rootScope.options.length && this.$rootScope.options.length > 0;
    }

    moreThanOneOptionAvailable() {
        return this.$rootScope.options && this.$rootScope.options.length && this.$rootScope.options.length > 1;
    }
}