import angular from 'angular';

export default class ListOverviewController {

    /*@ngInject*/
    constructor($scope, $rootScope, itemService, navigationService, $mdDialog, $q) {

        this.$q = $q;
        this.$rootScope = $rootScope;
        this.$mdDialog = $mdDialog;
        this.itemService = itemService;
        this.navigationService = navigationService;

        this.$rootScope.loading = true;
        this._initList();
        this._init();
        this._initDestroyListener($scope);
    }

    update() {

        this.$rootScope.loading = true;
        return this._initList(true);
    }

    clearList(ev) {

        return this._showClearListConfirmationDialog(ev).then(() => this._deleteDoneItems());
    }

    listIsEmpty() {

        return this.list && (!this.list.items || !this.list.items.length || !this.list.items.length > 0);
    }

    listDoesntContainsUndoneItems() {

        if (this.listIsEmpty()) {
            return true;
        }

        for (let i = 0; i < this.list.items.length; i++) {
            if (!this.list.items[i].done) {
                return false;
            }
        }

        return true;
    }

    setAllUndone() {

        for (let i = 0; i < this.list.items.length; i++) {
            if (this.list.items[i].done) {
                this.list.items[i].done = false;
                this.itemService.updateItemOfList(this.list.items[i], this.list);
            }
        }
    }

    setAllDone() {

        for (let i = 0; i < this.list.items.length; i++) {
            if (!this.list.items[i].done) {
                this.list.items[i].done = true;
                this.itemService.updateItemOfList(this.list.items[i], this.list);
            }
        }
    }

    _deleteDoneItems() {

        const promises = [];
        const notDoneItems = [];

        for (let i = 0; i < this.list.items.length; i++) {
            if (this.list.items[i].done) {
                promises.push(this.itemService.deleteItemOfList(this.list.items[i], this.list));
            } else {
                notDoneItems.push(this.list.items[i]);
            }
        }

        this.list.items.splice(0, this.list.items.length);
        this.list.items.push.apply(this.list.items, notDoneItems);

        return this.$q.all(promises);
    }

    _showClearListConfirmationDialog(ev) {

        const dialog = this.$mdDialog.confirm()
            .title('Möchtest du die Liste ' + this.list.name + ' wirklich leeren?')
            .content('Alle Posten auf der Liste, die als erledigt markiert wurden, werden unwiderruflich gelöscht.')
            .targetEvent(ev)
            .ok('Ja')
            .cancel('Nein');

        return this.$mdDialog.show(dialog);
    }

    _initList(refetch = false) {

        this.list = {
            name: 'Zusammenfassung',
            items: []
        };

        const allItems = this.itemService.getAllItems(refetch);

        return allItems.promise
            .then(() => {
                this.list.items = angular.copy(allItems);
                this.$rootScope.loading = false;
            }).catch(() => {
                this.$rootScope.loading = false;
            });
    }

    _init() {

        this.$rootScope.title = this.list.name;
        this.$rootScope.options = [
            {
                icon: '/img/icons/Toggle/ic_check_box_24px.svg',
                text: 'Alles erledigt',
                action: () => this.setAllDone(),
                disabled: () => this.listDoesntContainsUndoneItems()
            }, {
                icon: '/img/icons/Toggle/ic_check_box_outline_blank_24px.svg',
                text: 'Nichts erledigt',
                action: () => this.setAllUndone(),
                disabled: () => this.listIsEmpty() || !this.listDoesntContainsUndoneItems()
            }, {
                icon: '/img/icons/communication/ic_clear_all_24px.svg',
                text: 'Liste leeren',
                action: () => this.clearList(),
                disabled: () => this.listIsEmpty() || !this.listDoesntContainsUndoneItems()
            }
        ];

        this.$rootScope.shortCutAction = {
            parameters: '$mdOpenMenu, $event',
            icon: 'img/icons/notification/ic_sync_24px.svg',
            action: () => this.update(),
            available: true,
            ariaLabel: 'refetch current list from server'
        };
    }

    gotoLists() {
        this.navigationService.goto('/lists', true);
    }

    _initDestroyListener($scope) {

        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}