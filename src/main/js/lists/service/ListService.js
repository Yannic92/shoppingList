import RESTService from '../../global/RESTService';
export default class ListService {

    /*@ngInject*/
    constructor($resource, $filter, $q, $rootScope, shoppingListResourceConverter) {

        this.$q = $q;
        this.filter = $filter('filter');
        this.lists = [];

        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        const listsResource = $resource('/api/shoppingLists/:entityId', null, methods);
        this.restService = new RESTService($rootScope, $resource, listsResource, shoppingListResourceConverter,
            this.lists, this.filter);
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

        const existingList = this._findExistingList(listId);

        if(!existingList) {
            return this.getUpdatedShoppingList({entityId:listId});
        }

        if(!existingList.updated || refetch) {
            return this.getUpdatedShoppingList(existingList);
        }

        return this._getResolvedPromise(existingList);

    }

    getUpdatedShoppingList(list) {

        return this.restService.fetchOne({entityId: list.entityId})
            .then((shoppingList) => {
                shoppingList.updated = true;
                return shoppingList;
            });
    }

    createShoppingList(list) {
        return this.restService.create(list);
    }

    updateShoppingList(list) {
        return this.restService.update(list, {entityId: list.entityId});
    }

    deleteShoppingList(list) {
        return this.restService.delete({entityId: list.entityId});
    }

    deleteAllShoppingLists() {

        return this.restService.deleteAll();
    }

    static getDeleteMessage(list) {
        if (list && list.owners && list.owners.length > 1) {
            return 'Die Liste wäre nur für dich nicht mehr verfügbar. Solltest du sie zurück wollen, ' +
                'müsstest du dich an die restlichen Nutzer der Liste wenden.';
        } else {
            return 'Die Liste kann nicht wiederhergestellt werden.';
        }
    }

    _getResolvedPromise(resolvedData) {

        const deferred = this.$q.defer();
        const resolvedPromise = deferred.promise;
        deferred.resolve(resolvedData);

        return resolvedPromise;
    }

    _findExistingList(listId) {
        return this.filter(this.lists, {entityId: listId})[0];
    }
}