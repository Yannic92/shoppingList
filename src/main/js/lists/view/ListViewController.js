import angular from 'angular';
import {NewItemController} from '../../item/new/NewItemController';
import ListService from '../../api/list/ListService';
import Item from '../../item/Item';
import ShoppingList from '../ShoppingList';
export default class ListViewController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, itemService, $routeParams, articleService, $mdDialog, $q, navigationService, $timeout, $filter) {

        this.$q = $q;
        this.$filter = $filter;
        this.listService = listService;
        this.$rootScope = $rootScope;
        this.$mdDialog = $mdDialog;
        this.$timeout = $timeout;
        this.itemService = itemService;
        this.navigationService = navigationService;
        this.articleService = articleService;
        this.articles = this.articleService.getAllArticles();
        this.list = new ShoppingList({name: ''});
        this.$rootScope.loading = true;
        this.creating = false;
        this._init($routeParams);
        this._initDestroyListener($scope);

    }

    update() {
        this.$rootScope.loading = true;
        return this.listService.getUpdatedShoppingList(this.list)
            .then((updatedList) => {
                this.list = updatedList;
                this.$rootScope.loading = false;
            }).catch(() => {
                this.$rootScope.loading = false;
            });
    }

    selectedItemChanged(ev) {

        if (this.selectedArticle) {
            this.createNewItem(ev);
        }
    }

    createNewItem(ev) {

        if (this.selectedArticle) {

            this.newItem.article = this.selectedArticle;
        }

        if (this.newItem.article.name != '') {

            this._showNewItemDialog(ev)
                .then(item => {
                    return this.articleService.createArticle(item.article).then(() => item);
                })
                .then((item) => this._createItem(item))
                .then(() => {
                    this._initNewItem();
                    this._finishCreating();
                })
                .catch(() => this._finishCreating());
        }
    }

    clearList(ev) {

        return this._showClearListConfirmationDialog(ev).then(() => this._deleteDoneItems());
    }

    deleteList(ev) {
        this._showDeleteListConfirmationDialog(ev)
            .then(() => this.listService.deleteShoppingList(this.list))
            .then((indexOfDeletedList) => this._goToPreviousList(indexOfDeletedList));
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

    _createItem(item) {
        this.creating = true;
        return this.itemService.addItemToList(item, this.list);
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
        promises.push(this.listService.updateShoppingList(this.list));

        return this.$q.all(promises);
    }

    _finishCreating() {
        this.creating = false;
        this._setFocusToNewItemInput();
    }

    _setFocusToNewItemInput() {
        this.$timeout(() => {
            const itemInputElement = document.querySelector('#new-item-input');
            if(itemInputElement) {
                itemInputElement.focus();
            }
        });
    }

    clearFocusOnNewItemInput() {
        this.$timeout(() => {
            const itemInputElement = document.querySelector('#new-item-input');
            itemInputElement.blur();
        });
    }

    _goToPreviousList(indexOfCurrentList) {
        if (this.lists && this.lists.length && this.lists.length > 0) {
            const newIndex = indexOfCurrentList < this.lists.length ? indexOfCurrentList : indexOfCurrentList - 1;
            this.navigationService.goto('/lists/' + this.lists[newIndex].entityId, true);
        } else {
            this.navigationService.goto('/lists', true);
        }
    }

    _showNewItemDialog(ev) {
        return this.$mdDialog.show({
            controller: NewItemController,
            controllerAs: 'ctrl',
            locals: {newItem: this.newItem},
            templateUrl: 'templates/item/new/newItem.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true,
            fullscreen: false
        });
    }

    _showDeleteListConfirmationDialog(ev) {
        var dialog = this.$mdDialog.confirm()
            .title('Möchtest du die Liste ' + this.list.name + ' wirklich löschen?')
            .content(ListService.getDeleteMessage())
            .targetEvent(ev)
            .ok('Ja')
            .cancel('Nein');

        return this.$mdDialog.show(dialog);
    }

    _showClearListConfirmationDialog(ev) {

        var dialog = this.$mdDialog.confirm()
            .title('Möchtest du die Liste ' + this.list.name + ' wirklich leeren?')
            .content('Alle Posten auf der Liste, die als erledigt markiert wurden, werden unwiderruflich gelöscht.')
            .targetEvent(ev)
            .ok('Ja')
            .cancel('Nein');

        return this.$mdDialog.show(dialog);
    }

    _initNewItem() {
        this.newItem = new Item();
    }

    _gotoAllLists() {
        this.navigationService.goto('/lists', true);
    }

    _init($routeParams) {

        this._initList($routeParams.listId).then(() => {

            this.listService.onAnyListUpdate((updatedList) => {
                if(updatedList.entityId === this.list.entityId) {
                    this.list = updatedList;
                }
            });

            this.listService.onListUpdate(this.list, (updatedList) => {
                this.list = updatedList;
            });

            this._initNewItem();
            this._setFocusToNewItemInput();
            this._initRootScope();
        });
    }

    _initList(listId) {
        return this.listService.findShoppingListById(listId)
            .then((shoppingList) => {
                this.list = shoppingList;
            }, () => {
                this._gotoAllLists();
            }).catch(() => {
                this._gotoAllLists();
            });
    }

    _initRootScope() {
        this.$rootScope.loading = false;
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
            }, {
                icon: '/img/icons/action/ic_settings_24px.svg',
                text: 'Liste bearbeiten',
                link: '#/lists/' + this.list.entityId + '/edit'
            }, {
                icon: '/img/icons/action/ic_delete_24px.svg',
                text: 'Liste löschen',
                action: () => this.deleteList()
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

    _initDestroyListener($scope) {

        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}