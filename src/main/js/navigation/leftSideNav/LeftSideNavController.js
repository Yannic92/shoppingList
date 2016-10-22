export default class LeftSideNavController {

    /*@ngInject*/
    constructor($mdMedia, $mdComponentRegistry, $rootScope, navigationService){

        this.$mdMedia = $mdMedia;
        this.$mdComponentRegistry = $mdComponentRegistry;
        this.navigationService = navigationService;
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
        this.navigationService.goto(path, replace);
    }

    gotoExternal(path) {
        this.navigationService.gotoExternal(path);
    }

    isLockedOpen() {
        return this.$mdMedia('gt-sm');
    }

    _initRouteChangeSuccessListener($rootScope) {
        $rootScope.$on('$routeChangeSuccess', () => {
            this.closeNav();
        });
    }
}