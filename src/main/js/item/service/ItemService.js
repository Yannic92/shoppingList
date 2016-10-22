export default class ItemService {

    /*@ngInject*/
    constructor($resource, $filter, articleService) {

        this.$filter = $filter;
        this.articleService = articleService;

        this.itemsResource = $resource('/items/:id', null, {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        });

        this.items = [];
    }

    create(item) {
        return this.articleService.create(item.article)
            .then((createdArticle) => {
                item.article.entityId = createdArticle.entityId;
                return this.itemsResource.save(ItemService._toResource(item)).$promise;
            }).then((response) => {
                var responseEntity = ItemService._toEntity(response);
                this.items.push(responseEntity);
                return responseEntity;
            });
    }

    update(item) {
        return this.itemsResource.update({id: item.entityId}, ItemService._toResource(item)).$promise
            .then((response) => {
                var responseEntity = ItemService._toEntity(response);
                this._replaceExisting(responseEntity);
                return responseEntity;
            });
    }

    delete(item) {
        return this.itemsResource.delete({id: item.entityId}).$promise
            .then(() => {
                var existingList = this.$filter('filter')(this.items, {entityId: item.entityId})[0];
                var index = this.items.indexOf(existingList);
                this.items.splice(index, 1);
                return item;
            });
    }

    static _toResource(entity) {
        let resource = {};

        resource._links = entity._links;
        resource.entityId = entity.entityId;
        resource.count = entity.count;
        resource.article = {entityId: entity.article.entityId};
        resource.done = entity.done;

        return resource;
    }

    static _toEntity(resource) {
        var entity = {};

        entity.entityId = resource.entityId;
        entity._links = resource._links;
        entity.count = resource.count;
        entity.article = resource.article;
        entity.done = resource.done;

        return entity;
    }

    static _toEntities(resources) {

        var entities = [];
        if (!resources || !resources.length) {
            return entities;
        }

        for (var i = 0; i < resources.length; i++) {
            var entity = this._toEntity(resources[i]);
            entities.push(entity);
        }

        return entities;
    }

    _replaceExisting(item) {

        var existingItem = this.$filter('filter')(this.items, {entityId: item.entityId})[0];
        var index = this.items.indexOf(existingItem);
        this.items.splice(index, 1, item);
    }
}