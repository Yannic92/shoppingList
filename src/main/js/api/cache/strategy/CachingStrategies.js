import FastestCachingBehavior from './FastestCachingBehavior';
import BackgroundSyncBehavior from './BackgroundSyncBehavior';

class CachingStrategies {

    constructor() {
        this.fastestCachingBehavior = new FastestCachingBehavior(CachingStrategies.options.cache.name);
        this.backgroundSyncBehavior = new BackgroundSyncBehavior(CachingStrategies.options.cache.name);
    }

    fastest(request) {
        return this.fastestCachingBehavior.strategy(request);
    }

    backgroundSync(request) {
        return this.backgroundSyncBehavior.strategy(request);
    }
}

CachingStrategies.options = {
    cache: {
        name: 'shopping-list-dynamic-data-cache'
    }
};

export default CachingStrategies;