import RESTService from '../RESTService';
import EntityTypes from '../EntityTypes';

export default class ShoppingListRestService extends RESTService {

    /*@ngInject*/
    constructor($rootScope, $resource, $timeout, $q, shoppingListResourceConverter) {

        super($rootScope, $resource, $timeout, $q, shoppingListResourceConverter, 'list-cache-updated', EntityTypes.LIST);
    }
}