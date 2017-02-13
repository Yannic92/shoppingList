export default class CollectionUtils {

    static collectToContainer(newContent, container) {
        container.splice(0, container.length);
        container.push.apply(container, newContent);
    }

    static replaceExisting(filter, element, container, filterParameters) {

        const existingElement = filter(container, filterParameters)[0];
        const index = container.indexOf(existingElement);
        if(index >= 0) {
            container.splice(index, 1, element);
        }else {
            container.push(element);
        }

        return index;
    }

    static remove(filter, container, filterParameters) {

        const existingElement = filter(container, filterParameters)[0];
        const index = container.indexOf(existingElement);
        if(index >= 0) {
            container.splice(index, 1);
        }

        return index;
    }
}