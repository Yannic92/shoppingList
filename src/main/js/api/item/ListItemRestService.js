import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ListItemRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, itemResourceConverter) {

        super($rootScope, $resource, $timeout, $q, itemResourceConverter, 'item-cache-updated', EntityTypes.LIST_ITEM);
    }

    fetch(additionalParameters) {

        if(additionalParameters) {
            return super.fetch(additionalParameters);
        }
    }
}