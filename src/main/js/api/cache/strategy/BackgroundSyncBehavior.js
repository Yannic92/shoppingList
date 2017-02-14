import CachingBehavior from '../CachingBehavior';
import localforage from 'localforage';
import RequestSerializingService from '../RequestSerializingService';

export default class BackgroundSyncBehavior extends CachingBehavior {

    constructor(cacheName) {
        super(cacheName);

        self.addEventListener('sync', (event) => {
            if(event.tag === 'background-sync-request') {
                return this.handleBackgroundSync();
            }
        });

        this.db = localforage.createInstance({name: 'background-sync-request-db'});
    }

    strategy(request) {

        return this.networkOnly(request.clone())
            .then((result) => {
                self.registration.sync.register('background-sync-request');
                return result;
            }).catch(() => {
                return this.queueForBackgroundSync(request);
            });
    }

    queueForBackgroundSync(request) {

        if(self.registration) {
            const serializedRequest = RequestSerializingService.serialize(request);
            this.db.setItem(request.url, serializedRequest);
        }

        return new Response(['Queued for background sync'], {status: 200});
    }

    handleBackgroundSync() {

        const promises = [];
        this.db.iterate((request, url) => {
            promises.push(this.networkOnly(RequestSerializingService.deserialize(request))
                .then((response) => {
                    if(response.ok) {
                        this.db.removeItem(url);
                    }
                }));
        });

        return Promise.all(promises);
    }
}