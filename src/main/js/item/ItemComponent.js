import ItemController from './ItemController';
export default class ItemComponent {

    constructor() {
        this.templateUrl = '/templates/item/item.html';
        this.controller = ItemController;
        this.controllerAs = 'ctrl';
        this.bindings = {
            list: '=list',
            item: '=item'
        };
    }
}