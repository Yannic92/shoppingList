import RESTService from '../../global/RESTService';

export default class UserService {

    /*@ngInject*/
    constructor($resource, $q, $rootScope, userResourceConverter, $filter) {
        const userEndpoint = '/api/sLUsers/:username';
        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        const usersResource = $resource(userEndpoint, null, methods);
        this.users = [];

        this.restService = new RESTService($rootScope, $q, usersResource, userResourceConverter, this.users, $filter('filter'));
        this.usersConfirmationResource = $resource(userEndpoint + '/confirmation', null, methods);
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
     * @param {Boolean} refetch If true a request to the backend will be performed. If false the last fetched lists are
     *                  returned.
     * @returns {Array} All fetched {User}s.
     */
    getAllUsers(refetch = false) {
        if (refetch || this.usersAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.lists;
    }

    findByUsername(username) {

        return this.restService.fetchOne({username: username});
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
        return this.restService.delete({username: user.username});
    }

    confirmRegistrationFor(username, confirmation) {
        return this.usersConfirmationResource.update({username: username}, confirmation).$promise;
    }
}