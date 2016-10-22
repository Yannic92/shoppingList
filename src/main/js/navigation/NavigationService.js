export default class NavigationService {

    /*@ngInject*/
    constructor($window, $location, $anchorScroll) {
        this.$window = $window;
        this.$location = $location;
        this.$anchorScroll = $anchorScroll;
    }

    goToTopOfThePage() {
        this.$location.hash('top');
        this.$anchorScroll();
        this.$location.hash('');
    }

    goto(path, replace) {
        this.$location.path(path);
        if (replace) {
            this.$location.replace();
        }
    }

    gotoExternal(path) {
        this.$window.location.href = path;
    }

    reload() {
        this.$window.location.reload();
    }

    getCurrentPath() {
        return this.$location.path();
    }

    back() {
        this.$window.history.back();
    }
}