export default class AttributeEqualsFilter {

    static findFirstByMatchingAttribute(array, keyOfAttribute, expectedValueOfAttribute) {

        for(let index = 0; index < array.length; index++) {
            const element = array[index];
            if(element[keyOfAttribute] === expectedValueOfAttribute) {
                return {element: element, indexInArray: index};
            }
        }

        throw new Error('No element with value \'' + expectedValueOfAttribute + '\' for attribute \'' + keyOfAttribute + '\'');
    }
}