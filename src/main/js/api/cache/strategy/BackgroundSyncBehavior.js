import CachingBehavior from '../CachingBehavior';
import localforage from 'localforage';
import RequestSerializingService from '../RequestSerializingService';

export default class BackgroundSyncBehavior extends CachingBehavior {

    constructor(cacheName) {
        super(cacheName);
        this.backgroundSyncPromise = Promise.resolve('initialResolve');
        self.addEventListener('sync', (event) => {
            if(event.tag === 'network-connection-established') {
                return this.backgroundSyncPromise.then(() => {
                    this.backgroundSyncPromise = this.handleBackgroundSync();
                });
            }
        });

        this.db = localforage.createInstance({name: 'background-sync-request-db'});
    }

    strategy(request) {

        return this.queueForBackgroundSync(request).then(() => {
            return this.handleBackgroundSync();
        }).then(() => {
            return new Response(['Queued for background sync'], {status: 200});
        }).catch(() => {
            return new Response(['No background sync available'], {status: 502});
        });
    }

    queueForBackgroundSync(request) {

        if(self.registration) {
            const serializedRequest = RequestSerializingService.serialize(request);
            const timeStamp = Date.now();
            return this.db.setItem(timeStamp.toString(), serializedRequest);
        }
    }

    handleBackgroundSync() {

        const queue = [];
        return this.db.iterate((request, timeStamp) => {
            queue.push({request: request, timeStamp: parseInt(timeStamp)});
        }).then(() => {
            queue.sort((valueA, valueB) => {
                return valueA.timeStamp - valueB.timeStamp;
            });

            return this.workOffQueue(queue, 0);
        });
    }

    workOffQueue(queue, index) {

        if(index >= queue.length) {
            return Promise.resolve('finished');
        }

        const request = queue[index].request;
        const timeStamp = queue[index].timeStamp;
        return this.networkOnly(RequestSerializingService.deserialize(request))
            .then((response) => {
                if (response.ok) {
                    return this.db.removeItem(timeStamp.toString());
                }
            }).then(() => {
                return this.workOffQueue(queue, index + 1);
            }).catch(() => {
                //Still no network
            });

    }

}