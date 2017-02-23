import localforage from 'localforage';

export default class CredentialService {

    /*@ngInject*/
    constructor() {

        this.db = localforage.createInstance({name: 'user-db'});
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