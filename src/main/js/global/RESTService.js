import HALResource from '../services/HALResource';
import CollectionUtils from './CollectionUtils';

export default class RESTService {

    constructor(resource, resourceConverter, container, filter) {

        this.resource = resource;
        this.resourceConverter = resourceConverter;
        this.container = container;
        this.filter = filter;
    }

    create(newEntity) {

        return this.resource.save(this.resourceConverter.toResource(newEntity)).$promise
            .then((response) => {
                const responseEntity = this.resourceConverter.toEntity(response);
                this.container.push(responseEntity);
                return responseEntity;
            });
    }


    fetch() {

        this.container.fetching = true;
        this.container.fetched = false;

        this.container.promise = this.resource.get().$promise
            .then((response) => {
                this.container.fetching = false;
                this.container.fetched = true;
                const entities = this.resourceConverter.toEntities(HALResource.getContent(response));
                return CollectionUtils.collectToContainer(entities, this.container);
            });

        return this.container.promise;
    }

    update(entityToUpdate, pathVariablePattern) {

        const resourceToUpdate = this.resourceConverter.toResource(entityToUpdate);
        return this.resource.update(pathVariablePattern, resourceToUpdate).$promise
            .then((response) => {
                const responseEntity = this.resourceConverter.toEntity(response);
                CollectionUtils.replaceExisting(this.filter, responseEntity, this.container, pathVariablePattern);
                return responseEntity;
            });
    }

    delete(pathVariablePattern) {

        return this.resource.delete(pathVariablePattern).$promise
            .then(() => {
                return CollectionUtils.replaceExisting(this.filter, undefined, this.container, pathVariablePattern);
            });
    }
}