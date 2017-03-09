export default class ShoppingListResource {

    /**
     *
     * @param {ShoppingList} shoppingList
     */
    constructor(shoppingList) {
        this.entityId = shoppingList.entityId;
        this.name = shoppingList.name;
        this.lastModified = shoppingList.lastModified;
        this._initItems(shoppingList.items);
        this._initOwners(shoppingList.owners);
    }

    _initOwners(shoppingListOwners = []) {

        this.owners = [];

        shoppingListOwners.forEach(shoppingListOwner => this.owners.push({username: shoppingListOwner.username}));
    }

    _initItems(shoppingListItems = []) {

        this.items = [];

        shoppingListItems.forEach(shoppingListItem => this.items.push({entityId: shoppingListItem.entityId}));
    }
}
