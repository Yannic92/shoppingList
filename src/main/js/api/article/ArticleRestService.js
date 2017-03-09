import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ArticleRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, articleResourceConverter) {

        super($rootScope, $resource, $timeout, $q, articleResourceConverter, 'article-cache-updated', EntityTypes.ARTICLE);
    }
}