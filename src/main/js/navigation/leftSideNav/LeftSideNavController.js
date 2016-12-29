export default class LeftSideNavController {

    /*@ngInject*/
    constructor($mdMedia, $mdComponentRegistry, $rootScope, navigationService, authService){

        this.$mdMedia = $mdMedia;
        this.$mdComponentRegistry = $mdComponentRegistry;
        this.navigationService = navigationService;
        this.authService = authService;
        this._initRouteChangeSuccessListener($rootScope);
    }

    closeNav() {
        if (!this.$mdMedia('gt-sm')) {
            this.$mdComponentRegistry.when('leftNav').then((it) => {
                it.close();
            });
        }
    }

    goto(path, replace) {
        this.closeNav();
        this.navigationService.goto(path, replace);
    }

    gotoExternal(path) {
        this.navigationService.gotoExternal(path);
    }

    isLockedOpen() {
        return this.$mdMedia('gt-sm');
    }

    logout() {
        this.authService.logout()
            .then(() => {
                this.navigationService.goto('/login');
            });
    }

    _initRouteChangeSuccessListener($rootScope) {
        $rootScope.$on('$routeChangeSuccess', () => {
            this.closeNav();
        });
    }
}