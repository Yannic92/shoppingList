import ListService from './service/ListService';
export default class ListController {

    constructor($scope, $timeout, listService, navigationService, $mdDialog) {
        this.optionsShown = false;
        this.optionsVisible = false;
        this.optionsVisibleStopped = false;
        this.navigationService = navigationService;

        this.$mdDialog = $mdDialog;
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

    goto(path) {
        this.navigationService.goto(path);
    }

    _showDeleteConfirmationDialog(list, ev) {
        let dialog = this.$mdDialog.confirm()
            .title('Möchtest du die Liste ' + list.name + ' wirklich löschen?')
            .content(ListService.getDeleteMessage(list))
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