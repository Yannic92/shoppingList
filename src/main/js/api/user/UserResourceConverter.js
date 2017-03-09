import ResourceConverter from '../ResourceConverter';
import User from '../../user/User';
import UserResource from './UserResource';

/**
 *  Converts a JSON Object to a {User} and converts a {User} to an {UserResource}.
 *
 * @author Yannic Klem - mail@yannic-klem.de
 */
export default class UserResourceConverter extends ResourceConverter {

    /*@ngInject*/
    constructor() {
        super();
        this.ofJsonConverter = UserResourceConverter.ofJson;
        this.toJsonConverter = UserResourceConverter.toJson;
    }

    /**
     * Converts the given Json Object to an {Item}
     *
     * @param _links {Object} An Object containing all relation links.
     * @param username {String} The username of the {User}
     * @param firstName {String} The first name of the {User}
     * @param lastName {String} The last name of the {User}
     * @param email {String} The e-mail address of the {User}
     * @param authorities {Array}
     * @param password {String} The password of the {User}
     *
     * @return {User}
     */
    static ofJson({_links, username, firstName, lastName, email, authorities, password, lastModified}) {

        return new User({
            links:_links,
            username: username,
            firstName: firstName,
            lastName: lastName,
            email: email,
            authorities: authorities,
            password: password,
            lastModified: lastModified
        });
    }

    /**
     * Converts the given {User} to an {UserResource}
     *
     * @param {User} user
     * @returns {UserResource} The JSON representation of the given {User}
     */
    static toJson(user) {
        return new UserResource(user);
    }
}
