import AttributeEuqalsFilter from './AttributeEqualsFilter';

export default class CollectionUtils {

    static collectToContainer(newContent, container) {
        container.splice(0, container.length);
        container.push.apply(container, newContent);
    }

    static replaceExisting(element, container) {
        try {
            const index = CollectionUtils.getIndexOfElementIn(element, container);
            container.splice(index, 1, element);
        }catch (notFoundError) {
            return container.push(element);
        }
    }

    static remove(container, element) {

        try {
            const index = CollectionUtils.getIndexOfElementIn(element, container);
            container.splice(index, 1);
            return index;
        }catch (notFoundError) {
            return -1;
        }
    }

    static getIndexOfElementIn(element, container) {
        return AttributeEuqalsFilter.findFirstByMatchingAttribute(container, element.key, element[element.key]).indexInArray;
    }
}