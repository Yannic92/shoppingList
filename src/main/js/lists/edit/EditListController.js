import angular from 'angular';

export default class EditListController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, $filter, $routeParams, $mdToast, $mdDialog, userService, $timeout) {

        this.listService = listService;
        this.$rootScope = $rootScope;
        this.$mdToast = $mdToast;
        this.$mdDialog = $mdDialog;
        this.$timeout = $timeout;

        this.$rootScope.title = 'Einkaufsliste bearbeiten';
        this.$rootScope.loading = true;

        this.saveIsVisible = true;
        this.hideSave(0);
        this._initLists($filter, $routeParams);
        this._initUsers(userService);
        this._initDestroyListener($scope);
    }

    _initLists($filter, $routeParams) {
        this.list = {name: ''};

        this.listService.get($routeParams.id)
            .then((list) => {
                this.list = angular.copy(list);
                this.$rootScope.loading = false;
            });
    }

    _initUsers(userService) {
        this._initUserTextField();
        this.users = userService.get();
    }

    _initUserTextField() {
        this.userSearchText = '';
    }

    updateList() {
        this.$rootScope.loading = true;
        return this.listService.update(this.list)
            .then(() => this._showListUpdatedToast())
            .finally(() => this._resetForm());
    }

    _showListUpdatedToast() {

        let listUpdatedTost = this.$mdToast.simple()
            .content('Liste aktualisiert')
            .position('bottom right')
            .hideDelay(3000);

        this.$mdToast.show(listUpdatedTost);
    }

    _resetForm() {
        this.$rootScope.loading = false;
        this.hideSave(0);
        this.updateShoppingListForm.$setPristine();
    }

    addUserToOwners(selectedUser) {
        if (selectedUser) {
            this.list.owners.push(selectedUser);
            this.updateList()
                .then(() => this._initUserTextField);
        }
    }

    removeUserFromOwners(index, user) {
        if (this._isCurrentUser(user)) {

            this._showWarningBeforeRemovingOwnPermissions()
                .then(() => this._removeUserAtIndexFromOwnersList(index));
        } else {
            this._removeUserAtIndexFromOwnersList(index);
        }
    }

    triggerUpdateTimer() {

        if(this.updateTimer){
            this.$timeout.cancel(this.updateTimer);
        }

        this.updateTimer = this.$timeout(() => this.updateList(), 500);
    }

    _isCurrentUser(user) {
        return user && user.username === this.$rootScope.user.username;
    }

    _showWarningBeforeRemovingOwnPermissions() {

        let warningDialog = this.$mdDialog.confirm()
            .title('Warnung!')
            .content('Wenn du dich selbst aus der Liste der berechtigten Personen entfernst, kannst du dich nach dem ' +
                'Aktualisieren nicht selbst wieder hinzufügen. Dennoch fortfahren?')
            .ok('Ja')
            .cancel('Nein');

        return this.$mdDialog.show(warningDialog);
    }

    _removeUserAtIndexFromOwnersList(index) {
        this.list.owners.splice(index, 1);
        this.updateList();
    }

    nameChanged() {
        return this.updateShoppingListForm && this.updateShoppingListForm.$valid && !this.updateShoppingListForm.$pristine;
    }

    firstNameOrLastNameIsDefined(user) {
        return user.firstName || user.lastName;
    }

    showSave() {
        this.saveIsVisible = true;
    }

    hideSave(hideAfter = 350) {
        this.$timeout(() => {
            this.saveIsVisible = false;
        }, hideAfter);
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}