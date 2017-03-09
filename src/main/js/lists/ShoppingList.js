import Item from '../item/Item';

export default class ShoppingList {

    constructor({links = [], entityId, name, owners = [], items = [], lastModified = Date.now()} = {}) {

        this.key = 'entityId';
        this.links = links;
        this.entityId = entityId;
        this.name = name;
        this.owners = owners;
        this.items = this.getItems(items);
        this.lastModified = lastModified;
    }

    getItems(items) {
        const entityItems = [];

        items.forEach(item => {
            if(item instanceof  Item) {
                entityItems.push(item);
            }else {
                entityItems.push(new Item(item));
            }
        });

        return entityItems;
    }
}
