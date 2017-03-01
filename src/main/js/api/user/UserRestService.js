import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class UserRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, userResourceConverter) {

        super($rootScope, $resource, $timeout, userResourceConverter, 'user-cache-updated', EntityTypes.USER.endpoint);
    }
}