export default class ItemResource {

    constructor(item) {
        this._links = item.links;
        this.entityId = item.entityId;
        this.count = item.count;
        this.article = {entityId: item.article.entityId};
        this.done = item.done;
        this.lastModified = item.lastModified;
    }
}
