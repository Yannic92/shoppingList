import ShoppingList from '../ShoppingList';
export default class NewListController {

    /*@ngInject*/
    constructor($scope, $rootScope, listService, $mdToast, navigationService) {

        this.listService = listService;
        this.navigationService = navigationService;
        this.$rootScope = $rootScope;
        this.$mdToast = $mdToast;
        this.$rootScope.title = 'Neue Einkaufsliste';
        this.$rootScope.loading = false;
        this.list = new ShoppingList({name: ''});


        this.listCreatedToast = this.$mdToast.simple()
            .content('Neue Liste erstellt')
            .position('bottom right')
            .hideDelay(3000);

        this._initDestroyListener($scope);
    }

    createList() {
        this.$rootScope.loading = true;

        this.listService.createShoppingList(this.list)
            .then((createdList) => {
                this._showListCreatedToast();
                this.navigationService.goto('/lists/' + createdList.entityId, true);
                this.$rootScope.loading = false;
            })
            .catch(() => {
                this.$rootScope.loading = false;
            });
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