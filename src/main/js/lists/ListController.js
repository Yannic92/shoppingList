export default class ListController {

    constructor($scope, $timeout, listService) {
        this.optionsShown = false;
        this.optionsVisible = false;
        this.optionsVisibleStopped = false;

        this.$timeout = $timeout;
        this.listService = listService;

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

    deleteList(list, ev) {
        this._showDeleteConfirmationDialog(list, ev).then(() => {
            return this.listService.delete(list);
        });
    }

    _showDeleteConfirmationDialog(list, ev) {
        let dialog = this.$mdDialog.confirm()
            .title('Möchtest du die Liste ' + list.name + ' wirklich löschen?')
            .content(this.listService.getDeleteMessage(list))
            .targetEvent(ev)
            .ok('Ja')
            .cancel('Nein');

        return this.$mdDialog.show(dialog);
    }

    _initOptionsShownListener($scope) {
        $scope.$watch(() => this.optionsShown, (newValue, oldValue) => {

            if (oldValue && !newValue) {
                this.$timeout(() => {
                    this.optionActive = false;
                    this.hideOptionsOnElement();
                });
            }
        });
    }
}