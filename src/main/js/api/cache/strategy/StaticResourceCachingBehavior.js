import CachingBehavior from '../CachingBehavior';
export default class StaticResourceCachingBehavior extends CachingBehavior{

    constructor(self, cacheNamePrefix, cacheVersion, urlsToCache) {

        super(cacheNamePrefix + '-v' + cacheVersion);
        this.self = self;
        this.cacheNamePrefix = cacheNamePrefix;
        this.urlsToCache = urlsToCache;
        this.initListeners();
    }

    static updateFound() {
        return clients.matchAll().then(clients => {
            clients.forEach(client => client.postMessage('updateFound'));
        });
    }

    initListeners() {
        this._initInstallListener();
        this._initActivateListener();
    }

    _initInstallListener() {
        this.self.addEventListener('install', (event) => {
            this.self.skipWaiting();
            return event.waitUntil(
                this._registerCache()
            );
        });
    }

    _registerCache() {
        caches.keys().then(cacheNames => {
            if(!cacheNames.includes(this.cacheName)) {
                this.openCache().then(cache => {
                    cache.addAll(this.urlsToCache);
                });
            }
        });
    }

    _initActivateListener() {
        this.self.addEventListener('activate', (event) => {
            return event.waitUntil(
                caches.keys().then(cacheNames => this._deleteOldCaches(cacheNames))
            );
        });
    }

    _deleteOldCaches(cacheNames) {
        let updateFound = false;

        const existingCaches = cacheNames.filter((cacheName) => {
            return cacheName.startsWith(this.cacheNamePrefix);
        });

        return Promise.all(
            existingCaches.map(cacheName => {
                if(cacheName !== this.cacheName) {
                    updateFound = true;
                    return caches.delete(cacheName);
                }
            })).then(() => {
                if (updateFound) {
                    StaticResourceCachingBehavior.updateFound();
                }
            }
        );
    }
}