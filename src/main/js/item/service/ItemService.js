import RESTService from '../../global/RESTService';
export default class ItemService {

    /*@ngInject*/
    constructor($resource, $filter, articleService, itemResourceConverter) {

        this.articleService = articleService;

        const itemsResource = $resource('/items/:entityId', null, {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });

        this.items = [];

        this.restService = new RESTService(itemsResource, itemResourceConverter, this.items, $filter('filter'));
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
        return this.articleService.create(item.article)
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
}