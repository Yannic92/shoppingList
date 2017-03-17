/* global BroadcastChannel */
export default class CachingBehavior {

    constructor(cacheName) {
        this.cacheName = cacheName;
        this.networkStateChannel = new BroadcastChannel('network-state');
    }

    openCache() {
        return caches.open(this.cacheName);
    }

    networkOnly(request) {
        return fetch(request)
            .then((response) => {
                this._notifyAbobutSuccessfulRequest();
                return response;
            }, () => {
                this.networkStateChannel.postMessage('offline');
            });
    }

    _notifyAbobutSuccessfulRequest() {
        if( 'sync' in self.registration) {
            self.registration.sync.register('network-connection-established');
        }
        this.networkStateChannel.postMessage('online');
    }

    cacheOnly(request) {
        return this.openCache().then((cache) => {
            return cache.match(request);
        }).then(result => {
            if(!result) {
                return Promise.reject('Cache result was undefinied');
            }
            return result;
        });
    }

    fetchAndCache(request) {

        return this.networkOnly(request.clone()).then((response) => {
            if (request.method === 'GET' && response.ok) {
                this.cache(request, response);
            }
            return response.clone();
        });
    }

    cache(request, response) {
        return this.openCache().then((cache) => {
            return cache.put(request, response);
        });
    }

    strategy(request){
        throw 'Unsupported Operation: Could not handle request: ' + request.url;
    }
}