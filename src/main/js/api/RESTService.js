import HALResource from './HALResource';
import CollectionUtils from './CollectionUtils';
import EventTypes from './cache/event/EventTypes';
import uuid from 'uuid';

/* global BroadcastChannel */

export default class RESTService {

    constructor(rootScope, resource, timeout, resourceConverter, cacheUpdateChannelName, endpoint) {

        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.rootScope = rootScope;
        this.resource = resource(endpoint + '/:entityId', null, methods);
        this.resourceConverter = resourceConverter;
        this.container = [];
        this.timeout = timeout;
        this.cacheUpdateChannelName = cacheUpdateChannelName;
        this.entityUpdatedCallbacks = {};
        this.entitiesUpdatedCallbacks = [];
        this._initCacheEventsToHandle(endpoint);
        this._initCacheUpdateEventListener();
        this._initCacheOutdatedEventListener();
    }

    _initCacheEventsToHandle(endpoint) {
        if(endpoint.includes('/api/sLUsers')) {
            this.entityListUpdatedEvent = EventTypes.USERS_UPDATED;
            this.entityUpdatedEvent = EventTypes.USER_UPDATED;
        } else if(endpoint.includes('/api/shoppingLists')) {
            this.entityListUpdatedEvent = EventTypes.LISTS_UPDATED;
            this.entityUpdatedEvent = EventTypes.LIST_UPDATED;
        } else if(endpoint.includes('/api/items')) {
            this.entityListUpdatedEvent = EventTypes.ITEMS_UPDATED;
            this.entityUpdatedEvent = EventTypes.ITEM_UPDATED;
        } else if(endpoint.includes('/api/articles')) {
            this.entityListUpdatedEvent = EventTypes.ARTICLES_UPDATED;
            this.entityUpdatedEvent = EventTypes.ARTICLE_UPDATED;
        } else {
            throw 'unsupported entity';
        }
    }

    _initCacheUpdateEventListener() {

        this.broadcastChannel = new BroadcastChannel(this.cacheUpdateChannelName);

        this.broadcastChannel.addEventListener('message', (message) => {
            const event = message.data;
            if(event && event.eventType) {
                this.timeout(() => {this._handleCacheEvent(event);}, 0);
            }
        });
    }

    _initCacheOutdatedEventListener() {
        this.broadcastChannel = new BroadcastChannel('cache-outdated');

        this.broadcastChannel.addEventListener('message', (message) => {
            const event = message.data;
            if(event && event.eventType) {
                this.timeout(() => {this._handleCacheEvent(event);}, 0);
            }
        });
    }

    _handleCacheEvent(event) {

        switch (event.eventType.value) {
        case this.entityListUpdatedEvent.value:
            this._handleEntityListUpdatedEvent(event);
            break;
        case this.entityUpdatedEvent.value:
            this._handleEntityUpdatedEvent(event);
            break;
        case EventTypes.CACHE_OUTDATED.value:
            this.fetch();
            break;
        }
    }

    _handleEntityListUpdatedEvent(event) {
        const updatedEntityList = event.updatedEntity;
        CollectionUtils.collectToContainer(updatedEntityList, this.container);
        this.entitiesUpdatedCallbacks.forEach(callback => callback(updatedEntityList));
    }

    _handleEntityUpdatedEvent(event) {
        const updatedEntity = event.updatedEntity;
        CollectionUtils.replaceExisting(updatedEntity, this.container);
        const entityUpdatedCallbacks = this.entityUpdatedCallbacks[updatedEntity[updatedEntity.key]] || [];
        entityUpdatedCallbacks.forEach(callback => callback(updatedEntity));
    }

    create(newEntity) {

        newEntity.entityId = uuid.v4();
        return this.resource.save(this.resourceConverter.toResource(newEntity)).$promise
            .then(() => {
                this.container.push(newEntity);
                return newEntity;
            });
    }

    fetchOne(entity) {
        return this.resource.get(this._getPathVariablePattern(entity)).$promise
            .then((response) => {
                const responseEntity = this.resourceConverter.toEntity(response);
                CollectionUtils.replaceExisting(responseEntity, this.container);
                return responseEntity;
            });
    }

    fetch() {

        if(!this.rootScope.authenticated) {

            this.container.promise = Promise.reject('Not authenticated');
        }else if (!this.container.fetching) {

            this.container.fetching = true;
            this.container.fetched = false;

            this.container.promise = this.resource.get().$promise
                .then((response) => {
                    this.container.fetching = false;
                    this.container.fetched = true;
                    const entities = this.resourceConverter.toEntities(HALResource.getContent(response));
                    return CollectionUtils.collectToContainer(entities, this.container);
                });
        }

        return this.container.promise;
    }

    update(entityToUpdate) {

        const resourceToUpdate = this.resourceConverter.toResource(entityToUpdate);
        return this.resource.update(this._getPathVariablePattern(entityToUpdate), resourceToUpdate).$promise
            .then(() => {
                return entityToUpdate;
            });
    }

    delete(entity) {

        return this.resource.delete(this._getPathVariablePattern(entity)).$promise
            .then(() => {
                return CollectionUtils.remove(this.container, entity);
            });
    }

    deleteAll() {
        return this.resource.delete().$promise
            .then(() => this.fetch());
    }

    _getPathVariablePattern(entity) {
        return {
            entityId: entity[entity.key]
        };
    }

    onEntityUpdate(entity, callback) {
        let existingCallBacks = this.entityUpdatedCallbacks[entity[entity.key]];

        if(!existingCallBacks) {
            existingCallBacks = this.entityUpdatedCallbacks[entity[entity.key]] = [];
        }

        existingCallBacks.push(callback);
    }

    onEntitiesUpdate(callback) {
        this.entitiesUpdatedCallbacks.push(callback);
    }
}