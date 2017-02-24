import RESTService from '../RESTService';
import Endpoints from '../Endpoints';
import ShoppingList from '../../lists/ShoppingList';

export default class ListService {

    /*@ngInject*/
    constructor($resource, $rootScope, shoppingListResourceConverter, $timeout) {

        this.lists = [];

        this.restService = new RESTService($rootScope, $resource, shoppingListResourceConverter,
            this.lists, $timeout, 'list-cache-updated', Endpoints.list);
        this.timeout = $timeout;
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

        try {
            const existingList = this._findExistingList(listId);

            if(!existingList.updated || refetch) {
                return this.getUpdatedShoppingList(existingList);
            }

            return Promise.resolve(existingList);
        } catch(listNotFoundError) {

            return this.getUpdatedShoppingList(new ShoppingList({entityId: listId}));
        }

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

        return this.restService.deleteAll();
    }

    onListUpdate(list, callback) {
        this.restService.onEntityUpdate(list, callback);
    }

    onListsUpdate(callback) {
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