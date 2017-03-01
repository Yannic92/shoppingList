import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ItemRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, itemResourceConverter) {

        super($rootScope, $resource, $timeout, itemResourceConverter, 'item-cache-updated', EntityTypes.ITEM.endpoint);
    }
}