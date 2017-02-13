import ListService from '../api/list/ListService';
import ResponsiveListItemController from '../global/ResponsiveListItemController';

export default class ListController extends  ResponsiveListItemController{

    /*@ngInject*/
    constructor($scope, $timeout, listService, navigationService, $mdDialog) {

        super($scope, $timeout);

        this.navigationService = navigationService;
        this.$mdDialog = $mdDialog;
        this.listService = listService;
    }

    deleteList(list, ev) {
        this._showDeleteConfirmationDialog(list, ev).then(() => {
            return this.listService.deleteShoppingList(list);
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
}