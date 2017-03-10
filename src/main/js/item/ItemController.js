import ResponsiveListItemController from '../global/ResponsiveListItemController';

export default class ItemController extends ResponsiveListItemController{

    /*@ngInject*/
    constructor($scope, $timeout, itemService) {

        super($scope, $timeout);

        this.itemService = itemService;
    }

    deleteItem() {
        this.item.deleting = true;
        this.itemService.deleteItemOfList(this.item, this.list)
            .catch(() => {
                this.item.deleting = false;
            });
    }

    updateItem() {
        this.itemService.updateItemOfList(this.item, this.list);
    }
}