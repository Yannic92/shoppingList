import Endpoints from '../Endpoints';
import User from '../../user/User';

export default class UserService {

    /*@ngInject*/
    constructor($resource, userRestService) {

        const userEndpoint = Endpoints.user + '/:username';
        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.usersConfirmationResource = $resource(userEndpoint + '/confirmation', null, methods);


        this.restService = userRestService;
        this.users = this.restService.container;
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

    getCurrentUser() {

        return this.findByUsername('current');
    }

    findByUsername(username) {

        return this.restService.fetchOne(new User({username: username}));
    }

    usersAlreadyFetched() {
        return !this.users.fetching && !this.users.fetched;
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