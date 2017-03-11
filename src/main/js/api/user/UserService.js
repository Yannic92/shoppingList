import Endpoints from '../Endpoints';
import User from '../../user/User';
import CollectionUtils from '../CollectionUtils';

export default class UserService {

    /*@ngInject*/
    constructor($resource, userRestService, listOwnersRestService, shoppingListRestService) {

        const userEndpoint = Endpoints.user + '/:username';
        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.usersConfirmationResource = $resource(userEndpoint + '/confirmation', null, methods);


        this.restService = userRestService;
        this.listOwnersRestService = listOwnersRestService;
        this.shoppingListRestService = shoppingListRestService;
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

    addUserToOwnersOfList(user, list) {
        return this.listOwnersRestService.create(user, {listId: list.entityId})
            .then((createdUser) => {
                list.lastModified = Date.now();
                list.owners.push(user);
                this.shoppingListRestService.storeInDB(list);
                return createdUser;
            });
    }

    removeUserFromOwnersOfList(user, list) {
        return this.listOwnersRestService.delete(user, {listId: list.entityId})
            .then(() => {
                CollectionUtils.remove(list.owners, user);
                list.lastModified = Date.now();
                this.shoppingListRestService.storeInDB(list);
            });
    }

    confirmRegistrationFor(username, confirmation) {
        return this.usersConfirmationResource.update({username: username}, confirmation).$promise;
    }
}