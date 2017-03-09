export default class UserResource {

    /**
     *
     * @param {User} user
     */
    constructor(user) {
        this.username = user.username;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.password = user.password;
        this.authorities = user.authorities;
        this.lastModified = user.lastModified;
    }
}
