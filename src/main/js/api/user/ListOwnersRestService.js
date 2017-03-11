import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ListOwnersRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, userResourceConverter) {

        super($rootScope, $resource, $timeout, $q, userResourceConverter, 'user-cache-updated', EntityTypes.LIST_OWNER);
    }

    fetch(additionalParameters) {

        if(additionalParameters) {
            return super.fetch(additionalParameters);
        }
    }
}