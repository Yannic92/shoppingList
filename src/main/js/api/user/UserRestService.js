import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class UserRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, userResourceConverter) {

        super($rootScope, $resource, $timeout, $q, userResourceConverter, 'user-cache-updated', EntityTypes.USER);
    }
}