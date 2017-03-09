export default class User {

    constructor({links = [], username = '', firstName = '', lastName = '', email = '', authorities = [], password= '', lastModified = Date.now()} = {}) {

        this.key = 'username';
        this.links = links;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorities = authorities;
        this.password = password;
        this.lastModified = lastModified;
    }

    copy() {
        return new User({
            links: this.links,
            username: this.username,
            firstName: this.firstName,
            lastName: this.lastName,
            email: this.email,
            authorities: this.authorities,
            password: this.password,
            lastModified: this.lastModified
        });
    }
}
