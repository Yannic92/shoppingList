import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ItemRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, itemResourceConverter) {

        super($rootScope, $resource, $timeout, $q, itemResourceConverter, 'item-cache-updated', EntityTypes.ITEM);
    }
}