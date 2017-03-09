import localforage from 'localforage';
import User from '../user/User';

export default class CredentialService {

    /*@ngInject*/
    constructor() {

        this.db = localforage.createInstance({name: 'user-db'});
    }

    setCurrentUser(user) {
        return this.db.setItem('details', user);
    }

    getCurrentUser() {
        return this.db.getItem('details')
            .then(details => {
                if(details) {
                    return new User(details);
                } else {
                    throw new Error('No details for current user registered');
                }
            });
    }

    clearCurrentUser() {
        return this.db.removeItem('details');
    }

    storeCredentials(credentials) {
        return this.db.setItem('credentials', credentials);
    }

    getCredentials() {
        return this.db.getItem('credentials').then(credentials => {
            if(credentials) {
                return credentials;
            }else {
                throw new Error('No credentials found');
            }
        });
    }

    clearCredentials() {
        return this.db.removeItem('credentials');
    }
}