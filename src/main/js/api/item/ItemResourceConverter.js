import ResourceConverter from '../ResourceConverter';
import Item from '../../item/Item';
import ItemResource from './ItemResource';

/**
 *  Converts a JSON Object to an {Item} and converts an {Item} to an {ItemResource}.
 *
 * @author Yannic Klem - mail@yannic-klem.de
 */
export default class ItemResourceConverter extends ResourceConverter {

    /*@ngInject*/
    constructor() {
        super();
        this.ofJsonConverter = ItemResourceConverter.ofJson;
        this.toJsonConverter = ItemResourceConverter.toJson;
    }

    /**
     * Converts the given Json Object to an {Item}
     *
     * @param _links {Object} An Object containing all relation links.
     * @param entityId {Number} A number that identifies an {Item} uniquely.
     * @param article {Article} The Article this item refers to.
     * @param count {Number} The number of articles that should be bought.
     * @param done {Boolean} Indicates if the item is already bought or not.
     *
     * @returns {Item} An {Item} created out of the given JSON Object.
     */
    static ofJson({_links, entityId, article, count, done}) {

        return new Item({links:_links, entityId: entityId, article: article, count: count, done: done});
    }

    /**
     * Converts the given {Item} to an {ItemResource}
     *
     * @param {Item} item
     * @returns {ItemResource} The JSON representation of the given {Item}
     */
    static toJson(item) {
        return new ItemResource(item);
    }
}
