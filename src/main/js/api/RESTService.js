import HALResource from './HALResource';
import CollectionUtils from './CollectionUtils';
import EventTypes from './cache/event/EventTypes';
import uuid from 'uuid';
import localforage from 'localforage';

/* global BroadcastChannel */

export default class RESTService {

    constructor(rootScope, resource, timeout, $q, resourceConverter, cacheUpdateChannelName, entityType) {

        const methods = {
            'update': {method: 'PUT'},
            'delete': {method: 'DELETE'}
        };
        this.entityType = entityType;
        this.Promise = $q;
        this.db = localforage.createInstance({name: entityType.value});
        this.rootScope = rootScope;
        this.resource = resource(entityType.endpoint + '/:entityId', null, methods);
        this.resourceConverter = resourceConverter;
        this.container = [];
        this.timeout = timeout;
        this.cacheUpdateChannelName = cacheUpdateChannelName;
        this.entityUpdatedCallbacks = {};
        this.entitiesUpdatedCallbacks = [];
        this._initCacheUpdateEventListener();
        this._initCacheOutdatedEventListener();
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
        case EventTypes.CACHE_OUTDATED.value:
            //this.fetch();
            break;
        }
    }

    notifyAboutEntityUpdate(updatedEntity) {

        CollectionUtils.replaceExisting(updatedEntity, this.container);
        const entityUpdatedCallbacks = this.entityUpdatedCallbacks[updatedEntity[updatedEntity.key]] || [];
        entityUpdatedCallbacks.forEach(callback => callback(updatedEntity));
        this.entitiesUpdatedCallbacks.forEach(callback => callback(updatedEntity));
    }

    create(newEntity, additionalParameters = {}) {


        newEntity.lastModified = Date.now();
        newEntity.entityId = uuid.v4();
        return this.resource.save(additionalParameters, this.resourceConverter.toResource(newEntity)).$promise
            .then(() => {
                this.container.push(newEntity);
                this.storeInDB(newEntity);
                return newEntity;
            });
    }

    _findNewerVersion(result) {
        return result.sort((entityVersionOne, entityVersionTwo) => {
            return entityVersionTwo.lastModified - entityVersionOne.lastModified;
        })[0];
    }

    storeInDB(entity) {
        return this.db.setItem(entity[entity.key], entity).then(() => {
            this.notifyAboutEntityUpdate(entity);
            return entity;
        });
    }

    getFromDB(entity) {
        return this.db.getItem(entity[entity.key]).then(dbEntity => {
            if(dbEntity !== null) {
                return new this.entityType.entityConstructor(dbEntity);
            }

            return null;
        });
    }

    removeFromDB(entity) {
        return this.db.removeItem(entity[entity.key]);
    }

    fetchOne(entity) {

        return this.Promise((resolve, reject) => {

            let rejected = false;
            let finished = false;
            let results = [];
            let dbResult = null;

            const maybeReject = () => {
                if (finished) {
                    if (rejected) {
                        reject(new Error('Could not find entity.'));
                    } else {
                        doResolve();
                    }
                } else {
                    finished = true;
                    rejected = true;
                }
            };

            const maybeResolve = (entity) => {
                results.push(entity);
                if (finished) {
                    doResolve();
                } else {
                    finished = true;
                }
            };

            const doResolve = () => {
                const newerEntity = this._findNewerVersion(results);
                if(dbResult && newerEntity !== dbResult) {
                    this.storeInDB(newerEntity);
                }
                resolve(newerEntity);
            };

            this.resource.get(this._getPathVariablePattern(entity)).$promise
                .then((response) => {
                    const responseEntity = this.resourceConverter.toEntity(response);
                    CollectionUtils.replaceExisting(responseEntity, this.container);
                    maybeResolve(responseEntity);
                }, () => {
                    maybeReject();
                })
                .catch(() => {
                    maybeReject();
                });

            this.getFromDB(entity)
                .then(dbEntity => {
                    if (dbEntity !== null) {
                        dbResult = dbEntity;
                        maybeResolve(dbEntity);
                    } else {
                        maybeReject();
                    }
                })
                .catch(() => {
                    maybeReject();
                });

        });
    }

    fetch(additionalParameters = {}) {

        if(!this.rootScope.authenticated) {

            this.container.promise = this.Promise.reject('Not authenticated');
        }else if (!this.container.fetching) {

            this.container.fetching = true;
            this.container.fetched = false;

            this.container.promise = this.resource.get(additionalParameters).$promise
                .then((response) => {
                    this.container.fetching = false;
                    this.container.fetched = true;
                    const entities = this.resourceConverter.toEntities(HALResource.getContent(response));
                    this.container.splice(0, this.container.length);
                    return this._checkForUpdates(entities);
                }).then((entities) => {
                    CollectionUtils.collectToContainer(entities, this.container);
                    return this.container;
                });
        }

        return this.container.promise;
    }

    _checkForUpdates(entities) {
        const promises = [];

        entities.forEach((entity, index ) => {
            promises.push(this.getFromDB(entities[index]).then(dbEntity => {
                if(dbEntity === null || dbEntity.lastModified < entities[index].lastModified) {
                    return this.storeInDB(entities[index]);
                }

                return this.Promise.resolve(dbEntity);
            }));
        });

        return this.Promise.all(promises);
    }

    update(entityToUpdate, additionalParameters = {}) {

        entityToUpdate.lastModified = Date.now();
        const resourceToUpdate = this.resourceConverter.toResource(entityToUpdate);
        return this.resource.update(this._getPathVariablePattern(entityToUpdate, additionalParameters), resourceToUpdate).$promise
            .then(() => {
                CollectionUtils.replaceExisting(entityToUpdate, this.container);
                this.storeInDB(entityToUpdate);
                return entityToUpdate;
            });
    }

    delete(entity, additionalParameters = {}) {

        entity.lastModified = Date.now();
        return this.resource.delete(this._getPathVariablePattern(entity, additionalParameters)).$promise
            .then(() => this.removeFromDB(entity))
            .then(() => {
                entity.deleted = true;
                return CollectionUtils.remove(this.container, entity);
            });
    }

    deleteAll() {
        return this.resource.delete().$promise
            .then(() => this.fetch());
    }

    _getPathVariablePattern(entity, additionalParameters = {}) {

        additionalParameters.entityId = entity[entity.key];

        return additionalParameters;
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