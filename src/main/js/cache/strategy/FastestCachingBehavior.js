import CachingBehavior from '../CachingBehavior';
import toolbox from 'sw-toolbox';

/* global BroadcastChannel */

export default class FastestCachingBehavior extends CachingBehavior{

    constructor(cacheName) {
        super(cacheName);
        this.broadcastChannel = new BroadcastChannel('cache-updates');
    }

    strategy(request) {

        toolbox.fastest(request, {
            cache: {
                name: this.cacheName
            }
        });

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
                    console.debug('Received answer from network for request: ', request, result);
                    return this._handleSuccessfulNetworkResponse(result.clone(), resultContainer)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    console.debug('Rejected request from network: ', request, error);
                    return maybeReject(error);
                });

            this.cacheOnly(request)
                .then((result) => {
                    console.debug('Received answer from cache for request: ', request, result);
                    return this._handleSuccessfulCacheResponse(result.clone(), resultContainer)
                        .then(() => {
                            return maybeResolve(result);
                        });
                }, (error) => {
                    console.debug('Rejected request from cache: ', request, error);
                    return maybeReject(error);
                });
        });
    }

    _handleSuccessfulNetworkResponse(response, resultContainer) {
        return this._getBody(response)
            .then((body) => {
                resultContainer.networkResult = body;
            }).then(() => {
                if(resultContainer.cacheResult) {
                    return this.sendUpdateIfBodiesAreNotTheSame(resultContainer.cacheResult, resultContainer.networkResult);
                }
            }).then(() => {
                return response;
            });
    }

    _handleSuccessfulCacheResponse(response, resultContainer) {
        return this._getBody(response)
            .then((body) => {
                resultContainer.cacheResult = body;
            }).then(() => {
                if(resultContainer.networkResult) {
                    return this.sendUpdateIfBodiesAreNotTheSame(resultContainer.cacheResult, resultContainer.networkResult);
                }
            }).then(() => {
                return response;
            });
    }

    _getBody(response) {
        const textDecoder = new TextDecoder();
        const reader = response.body.getReader();

        return reader.read().then((result) => {
            return textDecoder.decode(result.value, {stream: true});
        });
    }

    sendUpdateIfBodiesAreNotTheSame(oldBody, newBody) {
        if(oldBody != newBody) {
            this.broadcastChannel.postMessage('Cache has been updated');
        }
    }
}