/* global BroadcastChannel */
export default class NavigationController {

    /*@ngInject*/
    constructor($rootScope, $scope, authService, $route, $mdComponentRegistry, $mdMedia, $timeout, $mdToast, navigationService) {
        this.lastPath = '';
        this.newVersionAvailable = false;
        this.initialLoad = true;

        this.$mdComponentRegistry = $mdComponentRegistry;
        this.$mdMedia = $mdMedia;
        this.$rootScope = $rootScope;
        this.authService = authService;
        this.$route = $route;
        this.$timeout = $timeout;
        this.$mdToast = $mdToast;
        this.$scope = $scope;
        this.navigationService = navigationService;

        this._initRouteChangeStartListener();
        this._initRouteChangeSuccessListener();
        this._initErrorListener();
        this._initUpdateReadyListener();
        this._initNetworkStateListener();
    }

    toggleNav() {
        this.$mdComponentRegistry.when('leftNav').then(function (it) {
            it.toggle();
        });
    }

    networkIsOffline() {
        return this.$rootScope.networkState === 'offline';
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
        const currentPath = this.navigationService.getCurrentPath();

        return currentPath && currentPath == '/lists';
    }

    redirectToLoginIfNotFreeRotue(newUrl) {
        if (!NavigationController._isFreeRoute(newUrl)) {

            if (NavigationController._urlIsDefined(newUrl)) {
                this.lastPath = newUrl;
            }
            this.navigationService.goto('/login', true);
        }
    }

    redirectToLoginIfAuthenticationRequired(newUrl) {
        this.$rootScope.routeIsLoading = true;
        this.authService.isAuthenticated()
            .then(() => {
                this.$route.reload();
                this.$rootScope.routeIsLoading = false;
            }, () => {
                this.redirectToLoginIfNotFreeRotue(newUrl);
                this.$rootScope.routeIsLoading = false;
            })
            .catch(() => {
                this.$rootScope.routeIsLoading = false;
            });
    }

    handleFinishedHistoryBackChain() {
        this.historyBackTimeout = this.$timeout(() => {
            this.processingHistoryBackChain = false;
            this.routeIsLoading = false;
            this.$rootScope.loading = false;
            this.navigationService.goto('/login', true);
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

            this.$rootScope.errorMessage = '';
            this.$rootScope.error = false;
            this.navigationService.goToTopOfThePage();

            const isRoot = this.isOnRoot();
            this.toggleButtonActive = isRoot && this.$rootScope.authenticated;
            this.backButtonActive = !isRoot && this.$rootScope.authenticated;
        });
    }

    _initErrorListener() {
        this.$rootScope.$watch('error', () => {
            if (this.$rootScope.error) {
                this.navigationService.goToTopOfThePage();
            }
        });
    }

    _initUpdateReadyListener() {

        if('serviceWorker' in navigator) {
            navigator.serviceWorker.addEventListener('message', (event) => {
                if(event.data === 'updateFound') {
                    this.newVersionAvailable = true;
                    this.$scope.$apply();
                }
            });
        }

        if (window.applicationCache) {
            window.applicationCache.addEventListener('updateready', function() {
                this.newVersionAvailable = true;
                this.$scope.$apply();
            });
        }
    }

    closeError() {
        this.$rootScope.error = false;
        this.$rootScope.errorMessage = '';
    }


    closeUpdateNotification() {
        this.newVersionAvailable = false;
    }

    reload() {
        this.navigationService.reload();
    }

    back() {
        this.navigationService.back();
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

    _initNetworkStateListener() {
        this.networkStateChannel = new BroadcastChannel('network-state');

        this.networkStateChannel.addEventListener('message', (message) => {
            this.$rootScope.networkState = message.data;
            this.$scope.$apply();
        });
    }
}