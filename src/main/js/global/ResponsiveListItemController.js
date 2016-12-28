export default class ResponsiveListItemController {

    constructor($scope, $timeout) {

        this.$timeout = $timeout;
        this.desktopOptionsVisible = false;
        this.touchOptionsActive = false;
        this._initTouchOptionsOpenListener($scope);
    }

    _initTouchOptionsOpenListener($scope) {
        $scope.$watch(() => this.touchOptionsOpen, (newValue, oldValue) => {
            if (oldValue && !newValue) {
                this.$timeout(() => {
                    this.hideTouchOptions();
                    this.hideDesktopOptions();
                });
            }
        });
    }

    showDesktopOptions() {
        this.desktopOptionsVisible = true;
    }

    hideDesktopOptions() {
        this.$timeout(() => {
            this.desktopOptionsVisible = false;
        }, 15);
    }

    showTouchOptions() {
        this.touchOptionsActive = true;

        this.$timeout(() => {
            this.touchOptionsOpen = true;
        }, 20);
    }

    hideTouchOptions() {
        this.$timeout(() => {
            this.touchOptionsActive = false;
        }, 500);
    }
}