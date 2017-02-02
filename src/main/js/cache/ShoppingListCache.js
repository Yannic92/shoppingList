export default class ShoppingListCache {

    constructor(self, cacheVersion, urlsToCache) {

        this.self = self;
        this.cacheName = 'shopping-list-cache-v' + cacheVersion;
        this.urlsToCache = urlsToCache;
    }

    static updateFound() {
        return clients.matchAll().then(clients => {
            clients.forEach(client => client.postMessage('updateFound'));
        });
    }

    initListeners() {
        this._initInstallListener();
        this._initActivateListener();
        this._initFetchListener();
    }

    _initInstallListener() {
        this.self.addEventListener('install', (event) => {
            return event.waitUntil(
                this._registerCache()
            );
        });
    }

    _registerCache() {
        return caches.open(this.cacheName).then(cache => cache.addAll(this.urlsToCache))
            .then(() => {
                this.self.skipWaiting();
            });
    }

    _initActivateListener() {
        this.self.addEventListener('activate', (event) => {
            event.waitUntil(
                caches.keys().then(cacheNames => this._deleteOldCaches(cacheNames))
            );
        });
    }

    _deleteOldCaches(cacheNames) {
        let updateFound = false;

        return Promise.all(
            cacheNames.map(cacheName => {
                if(cacheName !== this.cacheName) {
                    updateFound = true;
                    return caches.delete(cacheName);
                }
            })).then(() => {
                if (updateFound) {
                    ShoppingListCache.updateFound();
                }
            }
        );
    }

    _initFetchListener() {
        this.self.addEventListener('fetch', function(event) {
            event.respondWith(
                caches.match(event.request)
                    .then(function(response) {
                        // Cache hit - return response
                        if (response) {
                            return response;
                        }
                        return fetch(event.request);
                    })
            );
        });
    }
}