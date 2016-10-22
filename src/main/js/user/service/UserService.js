import HALResource from '../../services/HALResource';

export default class UserService {

    /*@ngInject*/
    constructor($resource, $q, $rootScope) {
        var userEndpoint = '/sLUsers/:username';
        var methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.usersResource = $resource(userEndpoint, null, methods);
        this.usersConfirmationResource = $resource(userEndpoint + '/confirmation', null, methods);
        this.users = [];
        this.$q = $q;
        this.$rootScope = $rootScope;
    }

    _getRejectedPromise(message) {
        const deferred = this.$q.defer();
        const rejectedPromise = deferred.promise;
        deferred.reject(message);

        return rejectedPromise;
    }

    get() {
        if (!this.users.fetching && !this.users.loaded) {
            this.fetch();
        }
        return this.users;
    }

    fetch() {
        if (this.$rootScope.authenticated) {
            this.users.fetching = true;
            this.users.promise = this.usersResource.get().$promise
                .then((response) => {
                    this._setUsers(HALResource.getContent(response));
                    this.users.loaded = true;
                    this.users.fetching = false;
                    return this.users;
                });
        } else {
            this.users.promise = this._getRejectedPromise('Not authenticated');
        }

        return this.users.promise;
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


    create(user) {
        return this.usersResource.save(user).$promise
            .then(function (response) {
                return HALResource.getContent(response);
            });
    }

    update(user) {
        return this.usersResource.update({username: user.username}, user).$promise
            .then(function (updatedUser) {
                return HALResource.getContent(updatedUser);
            });
    }

    delete(user) {
        return this.usersResource.delete({username: user.username}).$promise;
    }

    confirmRegistrationFor(username, confirmation) {
        return this.usersConfirmationResource.update({username: username}, confirmation).$promise;
    }

    _setUsers(users) {
        this.users.splice(0, this.users.length);
        this.users.push.apply(this.users, users);
    }
}