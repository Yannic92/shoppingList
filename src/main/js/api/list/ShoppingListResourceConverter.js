import ResourceConverter from '../ResourceConverter';
import ShoppingList from '../../lists/ShoppingList';
import ShoppingListResource from './ShoppingListResource';

/**
 *  Converts a JSON Object to a {ShoppingList} and converts a {ShoppingList} to an {ShoppingListResource}.
 *
 * @author Yannic Klem - mail@yannic-klem.de
 */
export default class ShoppingListResourceConverter extends ResourceConverter {

    /*@ngInject*/
    constructor(itemResourceConverter, userResourceConverter) {
        super();
        this.itemResourceConverter = itemResourceConverter;
        this.userResourceConverter = userResourceConverter;
        this.ofJsonConverter = this.ofJson;
        this.toJsonConverter = ShoppingListResourceConverter.toJson;
    }

    /**
     * Converts the given Json Object to an {Item}
     *
     * @param _links {Object} An Object containing all relation links.
     * @param entityId {Number} A number that identifies a {ShoppingList} uniquely.
     * @param name {String} the name of the {ShoppingList}.
     * @param owners {Array} An array of {User} who owns this {ShoppingList}.
     * @param items {Array} An array of {Item} that are contained in this {ShoppingList}.
     * @param lastModified {Number} Timestamp of last modification.
     * @return {ShoppingList}
     */
    ofJson({_links, entityId, name, owners, items, lastModified}) {

        return new ShoppingList({
            links:_links,
            entityId: entityId,
            name: name,
            owners: this.userResourceConverter.toEntities(owners),
            items: this.itemResourceConverter.toEntities(items),
            lastModified: lastModified
        });
    }

    /**
     * Converts the given {ShoppingList} to an {ShoppingListResource}
     *
     * @param {ShoppingList} shoppingList
     * @returns {ShoppingListResource} The JSON representation of the given {ShoppingList}
     */
    static toJson(shoppingList) {
        return new ShoppingListResource(shoppingList);
    }
}
