import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ShoppingListRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, shoppingListResourceConverter) {

        super($rootScope, $resource, $timeout, shoppingListResourceConverter, 'list-cache-updated', EntityTypes.LIST.endpoint);
    }
}