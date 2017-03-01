export default class ItemService {

    /*@ngInject*/
    constructor(articleService, itemRestService) {

        this.articleService = articleService;

        this.restService = itemRestService;
        this.items = this.restService.container;

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
}