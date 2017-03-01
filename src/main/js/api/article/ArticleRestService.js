import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ArticleRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, articleResourceConverter) {

        super($rootScope, $resource, $timeout, articleResourceConverter, 'article-cache-updated', EntityTypes.ARTICLE.endpoint);
    }
}