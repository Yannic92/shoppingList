export default class ListsController {

    /*@ngInject*/
    constructor($scope, $rootScope, $mdDialog, listService, navigationService) {
        $rootScope.title = 'Einkaufslisten';
        $rootScope.loading = true;
        this.listService = listService;
        this.navigationService = navigationService;

        this._initOptions($rootScope);
        this._initLists($rootScope);
        this._initDestroyListener($scope, $rootScope);
        this.deleteConfirmationDialog = new DeleteConfirmationDialog($mdDialog);
    }

    listsAreEmpty() {
        return !this.lists || !this.lists.length || this.lists.length == 0;
    }

    newList() {
        this.navigationService.goto('/newList');
    }

    deleteAllLists(ev) {
        this.deleteConfirmationDialog.show(ev).then(() => {
            return this.listService.deleteAll();
        });
    }

    _initLists($rootScope) {
        this.lists = this.listService.getAll();

        this.lists.promise.finally(() => {
            $rootScope.loading = false;
        });
    }

    _initOptions($rootScope) {
        $rootScope.options = [
            {
                icon: '/img/icons/communication/ic_clear_all_24px.svg',
                text: 'Alle Listen Löschen',
                action: () => this.deleteAllLists(),
                disabled: () => this.listsAreEmpty()
            }
        ];

        $rootScope.shortCutAction = {
            parameters: '$mdOpenMenu, $event',
            icon: 'img/icons/action/ic_add_shopping_cart_24px.svg',
            action: () => this.newList(),
            available: true,
            ariaLabel: 'Neue Liste erstellen'
        };
    }

    _initDestroyListener($scope, $rootScope) {
        $scope.$on('$destroy', () => {
            $rootScope.reset();
        });
    }
}

export class DeleteConfirmationDialog {

    constructor($mdDialog) {
        this.$mdDialog = $mdDialog;

        this.dialog = this.$mdDialog.confirm()
            .title('Möchtest du wirklich alle Listen löschen?')
            .content('Wenn du der einzige Besitzer einer der Listen bist, wird diese Liste unwiderruflich gelöscht.')
            .ok('Ja')
            .cancel('Nein');
    }

    show(ev) {
        this.dialog.targetEvent(ev);
        return this.$mdDialog.show(this.dialog);
    }
}