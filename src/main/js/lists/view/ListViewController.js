import angular from 'angular';
import {NewItemController} from '../../item/new/NewItemController';
import ListService from '../service/ListService';
import Article from '../../article/Article';
export default class ListViewController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, itemService, $routeParams, $filter, articleService, $mdDialog, $q, navigationService) {

        this.$q = $q;
        this.listService = listService;
        this.$rootScope = $rootScope;
        this.$mdDialog = $mdDialog;
        this.itemService = itemService;
        this.navigationService = navigationService;
        this.articles = articleService.get();
        this.list = {
            name: '',
            items: []
        };
        this.$rootScope.loading = true;
        this.creating = false;
        this._initNewItem();
        this._initLists($routeParams, $filter, $scope);
        this._initDestroyListener($scope);
    }

    update() {
        this.$rootScope.loading = true;
        return this.listService.getUpdated(this.list)
            .then((updatedList) => {
                this.list = updatedList;
            }).finally(() => {
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

        this.newItem.article = new Article(this.newItem.article.name.trim(), 0);

        if (this.newItem.article.name != '') {

            this._showNewItemDialog(ev)
                .then((item) => this._createItem(item))
                .then((createdItem) => this._addItemToList(createdItem))
                .then(() => this._initNewItem())
                .finally(() => this._finishCreating());
        }
    }

    clearList(ev) {

        return this._showClearListConfirmationDialog(ev).then(() => this._deleteDoneItems());
    }

    deleteList(ev) {
        this._showDeleteListConfirmationDialog(ev)
            .then(() => this.listService.delete(this.list))
            .then(() => this._goToPreviousList());
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
                this.itemService.update(this.list.items[i]);
            }
        }
    }

    setAllDone() {
        for (let i = 0; i < this.list.items.length; i++) {
            if (!this.list.items[i].done) {
                this.list.items[i].done = true;
                this.itemService.update(this.list.items[i]);
            }
        }
    }

    _createItem(item) {
        this.creating = true;
        return this.itemService.create(item);
    }

    _deleteDoneItems() {
        var promises = [];
        var notDoneItems = [];

        for (var i = 0; i < this.list.items.length; i++) {
            if (this.list.items[i].done) {
                promises.push(this.itemService.delete(this.list.items[i]));
            } else {
                notDoneItems.push(this.list.items[i]);
            }
        }

        this.list.items.splice(0, this.list.items.length);
        this.list.items.push.apply(this.list.items, notDoneItems);
        promises.push(this.listService.update(this.list));

        return this.$q.all(promises);
    }

    _finishCreating() {
        this.creating = false;
    }

    _goToPreviousList(lastIndex) {
        if (this.lists && this.lists.length && this.lists.length > 0) {
            const newIndex = lastIndex < this.lists.length ? lastIndex : lastIndex - 1;
            this.navigationService.goto('/lists/' + this.lists[newIndex].entityId, true);
        } else {
            this.navigationService.goto('/lists', true);
        }
    }

    _addItemToList(item) {
        this.list.items.push(item);
        return this.listService.update(this.list);
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
        this.newItem = {
            count: '',
            article: {
                name: '',
                priceInEuro: 0
            }
        };
    }

    _initLists($routeParams, $filter) {
        this.lists = this.listService.getAll();

        this.lists.promise
            .then(() => {
                if ($routeParams.listId) {
                    this.list = $filter('filter')(this.lists, {entityId: $routeParams.listId})[0];

                    if (!this.list) {
                        this.navigationService.goto('/lists', true);
                        return;
                    }

                    if (!this.list.updated) {
                        return this.update().then(() => this._init());
                    } else {
                        this._init();
                    }

                }
            }).finally(() => {
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
            }, {
                icon: 'img/icons/action/ic_settings_24px.svg',
                text: 'Liste bearbeiten',
                link: '#/lists/' + this.list.entityId + '/edit'
            }, {
                icon: 'img/icons/action/ic_delete_24px.svg',
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