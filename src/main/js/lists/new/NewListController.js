export default class NewListController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, $mdToast, userService, navigationService) {

        this.userService = userService;
        this.listService = listService;
        this.navigationService = navigationService;
        this.$rootScope = $rootScope;
        this.$mdToast = $mdToast;
        this.$rootScope.title = 'Neue Einkaufsliste';
        this.$rootScope.loading = false;
        this.userSearchText = '';
        this.list = {
            name: '',
            owners: [$rootScope.user]
        };

        this.users = this.userService.get();

        this.listCreatedToast = this.$mdToast.simple()
            .content('Neue Liste erstellt')
            .position('bottom right')
            .hideDelay(3000);

        this._initDestroyListener($scope);
    }

    hasProperty(user) {
        let filter = this.userSearchText;
        let concatenatedFirstAndLastName = null;
        if (user.firstName && user.lastName) {
            concatenatedFirstAndLastName = user.firstName + ' ' + user.lastName;
        }
        return user.username.toUpperCase().indexOf(filter.toUpperCase()) >= 0 ||
            (user.firstName && user.firstName.toUpperCase().indexOf(filter.toUpperCase()) >= 0) ||
            (user.lastName && user.lastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0) ||
            (concatenatedFirstAndLastName && concatenatedFirstAndLastName.toUpperCase().indexOf(filter.toUpperCase()) >= 0);
    }

    notContained(user) {
        for (let i = 0; i < this.list.owners.length; i++) {
            if (user.username == this.list.owners[i].username) {
                return false;
            }
        }

        return true;
    }

    static firstNameOrLastNameIsDefined(user) {

        return user.firstName || user.lastName;
    }

    createList() {
        this.$rootScope.loading = true;

        this.listService.create(this.list)
            .then((createdList) => {
                this._showListCreatedToast();
                this.navigationService.goto('/lists/' + createdList.entityId, true);
            })
            .finally(() => {
                this.$rootScope.loading = false;
            });
    }

    addUserToOwners(selectedUser) {
        if (selectedUser) {
            this.list.owners.push(selectedUser);
        }

        this.userSearchText = '';
    }

    removeUserFromOwners(index) {
        this.list.owners.splice(index, 1);
    }

    isLoggedInUser(user) {
        return user && user.username === this.$rootScope.user.username;
    }

    _showListCreatedToast() {
        return this.$mdToast.show(this.listCreatedToast);
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}