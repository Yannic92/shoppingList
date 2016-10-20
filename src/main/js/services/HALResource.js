export default class HALResource {

    static getContent(resource) {
        if (resource._embedded) {
            for (let key in resource._embedded) {
                return resource._embedded[key];
            }
        }
        return [];
    }

    static getResourceLinks(list) {
        const links = [];
        for (let i = 0; i < list.length; i++) {
            links.push(list[i]._links.self.href);
        }
        return links;
    }

    static getRelations(resource) {
        const relations = [];
        if (resource._links) {
            for (let key in resource._links) {
                if (key != 'self') {
                    relations.push(resource._links[key]);
                }
            }
        }
        return relations;
    }
}