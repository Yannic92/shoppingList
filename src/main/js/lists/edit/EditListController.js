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
        this.saveIsVisible = false;

        this._initLists($filter, $routeParams);
        this._initUsers(userService);
        this._initDestroyListener($scope);
    }

    _initLists($filter, $routeParams) {
        this.lists = this.listService.get();
        this.list = {name: ''};

        this.lists.promise
            .then(() => {
                this.list = angular.copy($filter('filter')(this.lists, {entityId: $routeParams.id})[0]);
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
            .then(this._showListUpdatedToast)
            .finally(this._resetForm);
    }

    _showListUpdatedToast() {

        let listUpdatedTost = this.$mdToast.simple()
            .content('Liste aktualisiert')
            .position('bottom right')
            .hideDelay(3000);

        this.$mdToast.show(listUpdatedTost);
    }

    _resetForm() {
        this.hideSave();
        this.$rootScope.loading = false;
        this.$scope.updateShoppingListForm.$setPristine();
    }

    addUserToOwners(selectedUser) {
        if (selectedUser) {
            this.list.owners.push(selectedUser);
            this.updateList()
                .then(this._initUserTextField);
        }
    }

    removeUserFromOwners(index, user) {
        if (this.isLoggedInUser(user)) {

            this._showWarningBeforeRemovingOwnPermissions()
                .then(this._removeUserAtIndexFromOwnersList(index));
        } else {
            this._removeUserAtIndexFromOwnersList(index);
        }
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

    hasProperty(user) {
        var filter = this.userSearchText;
        var concatenatedFirstAndLastName = null;
        if (user.firstName && user.lastName) {
            concatenatedFirstAndLastName = user.firstName + ' ' + user.lastName;
        }
        return user.username.toUpperCase().indexOf(filter.toUpperCase()) >= 0 ||
            (user.firstName && user.firstName.toUpperCase().indexOf(filter.toUpperCase()) >= 0) ||
            (user.lastName && user.lastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0) ||
            (concatenatedFirstAndLastName && concatenatedFirstAndLastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0);
    }

    notContained(user) {
        for (let index = 0; index < this.list.owners.length; index++) {
            if (user.username == this.list.owners[index].username) {
                return false;
            }
        }
        return true;
    }

    nameChanged(createShoppingListForm) {
        return createShoppingListForm && createShoppingListForm.$valid && !createShoppingListForm.$pristine;
    }

    firstNameOrLastNameIsDefined(user) {
        return user.firstName || user.lastName;
    }

    showSave() {
        this.saveIsVisible = true;
    }

    hideSave() {
        this.$timeout(() => {
            this.saveIsVisible = false;
        }, 100);
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}