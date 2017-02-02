export default class Item {

    constructor({links, entityId, article, count = undefined, done = false}) {

        this.links = links;
        this.entityId = entityId;
        this.article = article;
        this.count = count;
        this.done = done;

        this._validate();
    }

    _validate() {

        if(!this.links) {
            throw '\'links\' must not be undefined';
        }

        if(!this.entityId) {
            throw '\'entityId\' must not be undefined';
        }

        if(!this.article) {
            throw '\'article\' must not be undefined';
        }
    }
}
