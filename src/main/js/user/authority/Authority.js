export default class Authority {

    static get AVAILABLE_ROLES() {
        return [Authority.USER_ROLE];
    }

    static get USER_ROLE() {
        return 'USER';
    }

    /**
     *
     * @param {String} role use only available roles (Authority.AVAILABLE_ROLES)
     */
    constructor(role) {

        if(Authority.AVAILABLE_ROLES.indexOf(role) < 0) {
            throw 'Role \'' + role + '\' is not a valid role. Valid roles are: ' + Authority.AVAILABLE_ROLES;
        }

        this.authority = role;
    }
}