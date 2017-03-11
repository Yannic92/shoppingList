import Item from '../item/Item';
import User from '../user/User';

export default class ShoppingList {

    constructor({links = [], entityId, name, owners = [], items = [], lastModified = Date.now()} = {}) {

        this.key = 'entityId';
        this.links = links;
        this.entityId = entityId;
        this.name = name;
        this.owners = this.getOwners(owners);
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

    getOwners(owners) {
        const entityOwners = [];

        owners.forEach(user => {
            if(user instanceof  User) {
                entityOwners.push(user);
            }else {
                entityOwners.push(new User(user));
            }
        });

        return entityOwners;
    }
}
