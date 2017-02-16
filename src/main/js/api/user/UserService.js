import RESTService from '../RESTService';
import Endpoints from '../Endpoints';
import User from '../../user/User';

export default class UserService {

    /*@ngInject*/
    constructor($resource, $q, $rootScope, userResourceConverter, $filter, $timeout) {

        const userEndpoint = Endpoints.user + '/:username';
        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.usersConfirmationResource = $resource(userEndpoint + '/confirmation', null, methods);

        this.users = [];

        this.restService = new RESTService($rootScope,$q, $resource, userResourceConverter,
            this.users, $filter('filter'), $timeout, 'user-cache-updated', Endpoints.user);
    }

    _getRejectedPromise(message) {
        const deferred = this.$q.defer();
        const rejectedPromise = deferred.promise;
        deferred.reject(message);

        return rejectedPromise;
    }

    /**
     * Returns all {User}s.
     *
     * @param {Boolean} refetch If true a request to the backend will be performed. If false the last fetched users are
     *                  returned.
     * @returns {Array} All fetched {User}s.
     */
    getAllUsers(refetch = false) {
        if (refetch || this.usersAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.users;
    }

    findByUsername(username) {

        return this.restService.fetchOne(new User({username: username}));
    }

    usersAlreadyFetched() {
        return !this.users.fetching && !this.users.fetched;
    }

    storeCredentials(credentials) {
        localStorage.setItem('credentials', JSON.stringify(credentials));

    }

    getCredentials() {
        return JSON.parse(localStorage.getItem('credentials') || '{}');
    }

    clearCredentials() {
        localStorage.setItem('credentials', JSON.stringify({}));
    }


    createUser(user) {

        return this.restService.create(user);
    }

    updateUser(user) {
        return this.restService.update(user);
    }

    deleteUser(user) {
        return this.restService.delete(user);
    }

    confirmRegistrationFor(username, confirmation) {
        return this.usersConfirmationResource.update({username: username}, confirmation).$promise;
    }
}