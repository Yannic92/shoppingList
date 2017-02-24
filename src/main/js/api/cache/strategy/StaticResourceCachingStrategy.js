export default class StaticResourceCachingStrategy {

    constructor(staticResourceCaches = []) {
        this.staticResourceCaches = staticResourceCaches;
    }

    strategy(request) {

        return new Promise((resolve) => {

            let rejectCounter = 0;

            const maybeReject = () => {
                rejectCounter++;
                if(rejectCounter >= this.staticResourceCaches.length) {
                    resolve(fetch(request));
                }
            };

            const maybeResolve = (result) => {
                if (result instanceof Response) {
                    resolve(result);
                } else {
                    maybeReject('No result returned');
                }
            };

            this.staticResourceCaches.forEach(staticResourcesCache => {
                staticResourcesCache.cacheOnly(request)
                    .then(
                        response =>  maybeResolve(response),
                        error => maybeReject(error)
                    );
            });
        });
    }
}
