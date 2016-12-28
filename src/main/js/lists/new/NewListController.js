export default class NewListController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, $mdToast, userService, navigationService) {

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

        this._initUsers(userService);

        this.listCreatedToast = this.$mdToast.simple()
            .content('Neue Liste erstellt')
            .position('bottom right')
            .hideDelay(3000);

        this._initDestroyListener($scope);
    }

    firstNameOrLastNameIsDefined(user) {

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
            this._initUserTextField();
        }
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

    _initUsers(userService) {
        this._initUserTextField();
        this.users = userService.get();
    }

    _initUserTextField() {
        this.userSearchText = '';
    }
    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {
            this.$rootScope.reset();
        });
    }
}