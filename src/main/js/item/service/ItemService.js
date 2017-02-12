import RESTService from '../../global/RESTService';
export default class ItemService {

    /*@ngInject*/
    constructor($q, $rootScope, $resource, $filter, articleService, itemResourceConverter) {

        this.$q = $q;
        this.$rootScope = $rootScope;
        this.articleService = articleService;

        const itemsResource = $resource('/api/items/:entityId', null, {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });

        this.items = [];

        this.restService = new RESTService($rootScope, $q, itemsResource, itemResourceConverter, this.items, $filter('filter'));
    }

    /**
     * Returns all items.
     *
     * @param {Boolean} refetch If true a request to the backend will be performed. If false the last fetched lists are
     *                  returned.
     * @returns {Array} All Items
     */
    getAllItems(refetch = false) {
        if (refetch || this.itemsAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.items;
    }

    itemsAlreadyFetched() {
        return !this.items.fetching && !this.items.fetched;
    }

    createItem(item) {
        return this.articleService.createArticle(item.article)
            .then((createdArticle) => {
                item.article.entityId = createdArticle.entityId;
                return this.restService.create(item);
            });
    }

    updateItem(item) {
        return this.restService.update(item, {entityId: item.entityId});
    }

    deleteItem(item) {
        return this.restService.delete({entityId: item.entityId});
    }

    _fetch() {
        if(!this.$rootScope.authenticated) {
            this.items.promise = this._getRejectedPromise('Not authenticated');
        }else if (!this.items.fetching) {
            this.restService.fetch();
        }

        return this.items.promise;
    }


    _getRejectedPromise(message) {
        const deferred = this.$q.defer();
        const rejectedPromise = deferred.promise;
        deferred.reject(message);

        return rejectedPromise;
    }
}