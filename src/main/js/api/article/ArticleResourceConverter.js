import ResourceConverter from '../ResourceConverter';
import Article from '../../article/Article';
import ArticleResource from './ArticleResource';

/**
 *  Converts a JSON Object to an {Article} and converts an {Article} to an {ArticleResource}.
 *
 * @author Yannic Klem - mail@yannic-klem.de
 */
export default class ArticleResourceConverter extends ResourceConverter {

    /*@ngInject*/
    constructor() {
        super();
        this.ofJsonConverter = ArticleResourceConverter.ofJson;
        this.toJsonConverter = ArticleResourceConverter.toJson;
    }

    /**
     * Converts the given Json Object to an {Article}
     *
     * @param name The name of the {Article}
     * @param priceInEuro The price in euro of the {Article}
     * @param _links {Object} An Object containing all relation links.
     * @param entityId {Number} A number that identifies an {Article} uniquely.
     *
     * @returns {Article} An {Item} created out of the given JSON Object.
     */
    static ofJson({name, priceInEuro, entityId, _links, lastModified}) {

        return new Article({name, priceInEuro, entityId, links: _links, lastModified});
    }

    /**
     * Converts the given {Article} to an {ArticleResource}
     *
     * @param {Article} article
     * @returns {ArticleResource} The JSON representation of the given {Article}
     */
    static toJson(article) {
        return new ArticleResource(article);
    }
}
