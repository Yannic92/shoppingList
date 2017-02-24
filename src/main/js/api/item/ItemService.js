import RESTService from '../RESTService';
import Endpoints from '../Endpoints';
export default class ItemService {

    /*@ngInject*/
    constructor($rootScope, $resource, articleService, itemResourceConverter, $timeout) {

        this.$rootScope = $rootScope;
        this.articleService = articleService;

        this.items = [];

        this.restService = new RESTService($rootScope, $resource, itemResourceConverter, this.items, $timeout, 'item-cache-updated', Endpoints.item);
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
        return this.restService.update(item);
    }

    deleteItem(item) {
        return this.restService.delete(item);
    }

    _fetch() {
        if(!this.$rootScope.authenticated) {
            this.items.promise = Promise.reject('Not authenticated');
        }else if (!this.items.fetching) {
            this.restService.fetch();
        }

        return this.items.promise;
    }
}