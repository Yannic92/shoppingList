export function UserAlreadyContainedFilter() {
    return function (users, containingArray) {

        return users.filter((user) => {
            for (let index = 0; index < containingArray.length; index++) {
                if (user.username == containingArray[index].username) {
                    return false;
                }
            }
            return true;
        });
    };
}