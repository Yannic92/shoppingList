import ShoppingList from '../../lists/ShoppingList';

export default class ListService {

    /*@ngInject*/
    constructor(shoppingListRestService, $q) {


        this.restService = shoppingListRestService;
        this.lists = this.restService.container;
        this.Promise = $q;
    }

    /**
     * Returns all {ShoppingList}s.
     *
     * @param {Boolean} refetch If true a request to the backend will be performed. If false the last fetched lists are
     *                  returned.
     * @returns {Array} All fetched {ShoppingLists}.
     */
    getAllShoppingLists(refetch = false) {
        if (refetch || this.shoppingListsAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.lists;
    }

    shoppingListsAlreadyFetched() {
        return !this.lists.fetching && !this.lists.fetched;
    }

    findShoppingListById(listId, refetch = false) {

        let promise;
        try {
            const existingList = this._findExistingList(listId);

            if(!existingList.updated || refetch) {
                promise = this.getUpdatedShoppingList(existingList);
            }else {
                promise = existingList;
            }
        } catch(listNotFoundError) {

            promise = this.getUpdatedShoppingList(new ShoppingList({entityId: listId}));
        }

        return promise;

    }

    getUpdatedShoppingList(list) {

        return this.restService.fetchOne(list)
            .then((shoppingList) => {
                shoppingList.updated = true;
                return shoppingList;
            });
    }

    createShoppingList(list) {
        return this.restService.create(list);
    }

    updateShoppingList(list) {
        return this.restService.update(list);
    }

    deleteShoppingList(list) {
        return this.restService.delete(list);
    }

    deleteAllShoppingLists() {

        const promises = [];

        this.lists.forEach(list => {
            promises.push(this.deleteShoppingList(list));
        });

        return this.Promise.all(promises);
    }

    onListUpdate(list, callback) {
        this.restService.onEntityUpdate(list, callback);
    }

    onAnyListUpdate(callback) {
        this.restService.onEntitiesUpdate(callback);
    }

    static getDeleteMessage(list) {
        if (list && list.owners && list.owners.length > 1) {
            return 'Die Liste wäre nur für dich nicht mehr verfügbar. Solltest du sie zurück wollen, ' +
                'müsstest du dich an die restlichen Nutzer der Liste wenden.';
        } else {
            return 'Die Liste kann nicht wiederhergestellt werden.';
        }
    }

    _findExistingList(listId) {
        this.lists.forEach(list => {
            if(list.entityId === listId) {
                return list;
            }
        });

        throw new Error('List with id \'' + listId + '\' could not be found');
    }
}