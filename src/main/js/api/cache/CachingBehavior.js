export default class CachingBehavior {

    constructor(cacheName) {
        this.cacheName = cacheName;
    }

    openCache() {
        return caches.open(this.cacheName);
    }

    networkOnly(request) {
        return fetch(request).then((response) => {
            if( 'sync' in self.registration) {
                self.registration.sync.register('network-connection-established');
            }
            return response;
        });
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
            if (request.method === 'GET' && this.isSuccessful(response)) {
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

    isSuccessful(response) {
        return response.status >= 200 && response.status < 300;
    }
}