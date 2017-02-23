export default class BasicAuthUtils {

    static buildAuthorizationHeader(username, password) {
        return 'Basic ' + btoa(username + ':' + password);
    }
}
