export function UserPropertyFilter() {
    return function (users, userFilterText) {

        return users.filter((user) => {
            var concatenatedFirstAndLastName = null;
            if (user.firstName && user.lastName) {
                concatenatedFirstAndLastName = user.firstName + ' ' + user.lastName;
            }
            return user.username.toUpperCase().indexOf(userFilterText.toUpperCase()) >= 0 ||
                (user.firstName && user.firstName.toUpperCase().indexOf(userFilterText.toUpperCase()) >= 0) ||
                (user.lastName && user.lastName.toUpperCase().indexOf(userFilterText.toUpperCase()) >= 0) ||
                (concatenatedFirstAndLastName && concatenatedFirstAndLastName.toUpperCase().indexOf(userFilterText.toUpperCase()) >= 0);
        });
    };
}