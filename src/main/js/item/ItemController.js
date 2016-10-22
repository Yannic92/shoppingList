export default class ItemController {

    /*@ngInject*/
    constructor($scope, itemService, $timeout) {

        this.$timeout = $timeout;
        this.itemService = itemService;

        this.list = $scope.$parent.$parent.$parent.ctrl.list;
        this.optionsVisible = false;
        this.optionsVisibleStopped = false;
        this._initOptionsShownListener($scope);
    }

    showOptionsOnElement() {
        if (!this.optionsVisibleStopped) {
            this.optionsVisible = true;
        }
    }

    hideOptionsOnElement() {
        this.optionsVisibleStopped = true;
        this.optionsVisible = false;

        this.$timeout(() => {
            this.optionsVisibleStopped = false;
        }, 15);
    }

    showOptions() {
        this.optionActive = true;

        this.$timeout(() => {
            this.optionsShown = true;
            this.hideOptionsOnElement();
        }, 20);
    }

    hideOptions() {
        this.optionsShown = false;
    }

    deleteItem(item) {

        item.deleting = true;
        this.itemService.delete(item)
            .then(() => {
                var index = this.list.items.indexOf(item);
                this.list.items.splice(index, 1);
                return item;
            })
            .finally(() => {
                item.deleting = false;
            });

    }

    updateItem(item) {

        this.itemService.update(item);
    }

    _initOptionsShownListener($scope) {
        $scope.$watch('optionsShown', (newValue, oldValue) => {
            if (oldValue && !newValue) {
                this.$timeout(() => {
                    this.optionActive = false;
                    this.hideOptionsOnElement();
                });
            }
        });

    }
}