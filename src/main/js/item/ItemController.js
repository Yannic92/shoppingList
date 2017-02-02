import ResponsiveListItemController from '../global/ResponsiveListItemController';

export default class ItemController extends ResponsiveListItemController{

    /*@ngInject*/
    constructor($scope, $timeout, itemService) {

        super($scope, $timeout);

        this.itemService = itemService;
    }

    deleteItem() {
        this.item.deleting = true;
        this.itemService.deleteItem(this.item)
            .then(() => this._removeItemFromList())
            .finally(() => {
                this.item.deleting = false;
            });
    }

    _removeItemFromList() {
        const index = this.list.items.indexOf(this.item);
        this.list.items.splice(index, 1);
        return this.item;
    }

    updateItem() {
        this.itemService.updateItem(this.item);
    }
}