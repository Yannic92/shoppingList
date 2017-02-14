import CachingBehavior from '../CachingBehavior';
import EntityTypes from '../../EntityTypes';
import UpdateNotificationService from '../UpdateNotificationService';
import ReadableStreamBodyReader from '../../ReadableStreamBodyReader';

export default class FastestCachingBehavior extends CachingBehavior{

    constructor(cacheName) {
        super(cacheName);
        this.updateNotificationService = new UpdateNotificationService();
        this.readableStreamBodyReader = new ReadableStreamBodyReader();
    }

    strategy(request) {

        return new Promise((resolve, reject) => {
            let rejected = false;
            const reasons = [];

            const maybeReject = (reason) => {
                reasons.push(reason.toString());
                if (rejected) {
                    reject(new Error('Both cache and network failed: \'' +
                        reasons.join('\', \'') + '\''));
                } else {
                    rejected = true;
                }
            };

            const maybeResolve = (result) => {
                if (result instanceof Response) {
                    resolve(result);
                } else {
                    maybeReject('No result returned');
                }
            };

            const resultContainer = {
                networkResult: null,
                cacheResult: null
            };

            this.fetchAndCache(request.clone())
                .then((result) => {
                    const entityType = EntityTypes.ofUrl(request.url);
                    return this._handleSuccessfulResponse(result.clone(), resultContainer, 'networkResult', entityType)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    return maybeReject(error);
                });

            this.cacheOnly(request)
                .then((result) => {
                    const entityType = EntityTypes.ofUrl(request.url);
                    return this._handleSuccessfulResponse(result.clone(), resultContainer, 'cacheResult', entityType)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    return maybeReject(error);
                });
        });
    }

    _handleSuccessfulResponse(response, resultContainer, keyToStore, entityType) {
        return this.readableStreamBodyReader.read(response.body)
            .then((body) => {
                resultContainer[keyToStore] = body;
            }).then(() => {
                if(FastestCachingBehavior._cacheUpdated(resultContainer)){
                    this.updateNotificationService.sendCacheUpdateNotification(entityType, resultContainer.networkResult);
                }
            }).then(() => {
                return response;
            });
    }

    static _cacheUpdated(resultContainer) {
        return (resultContainer.cacheResult && resultContainer.networkResult) &&
            (resultContainer.cacheResult != resultContainer.networkResult);
    }
}

