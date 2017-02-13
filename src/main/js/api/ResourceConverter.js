//Do NOT instantiate this class
export default class ResourceConverter {

    constructor() {
        this.ofJsonConverter = () => {
            throw 'Converter is not initialized. Please dont use ResourceConverter itself. Extend it and initialize' +
            '\'this.ofJsonConverter\' in the constructor with a function that converts the JSON Resource to the entity.';
        };

        this.toJsonConverter = () => {
            throw 'Converter is not initialized. Please dont use ResourceConverter itself. Extend it and initialize' +
            '\'this.toJsonConverter\' in the constructor with a function that converts the entity to the JSON Resource.';
        };
    }

    toEntity(resource) {

        return this.ofJsonConverter(resource);
    }

    toEntities(resources) {

        const entities = [];
        if (!resources || !resources.length) {
            return entities;
        }

        resources.forEach((resource) => {
            entities.push(this.toEntity(resource));
        });

        return entities;
    }

    toResource(entity) {

        return this.toJsonConverter(entity);
    }

    toResources(entities) {

        const resources = [];

        if(!entities || !entities.length) {
            return resources;
        }

        entities.forEach((entity) => {
            resources.push(this.toResource(entity));
        });

        return resources;
    }
}
