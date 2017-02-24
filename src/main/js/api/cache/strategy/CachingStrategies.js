import FastestCachingBehavior from './FastestCachingBehavior';
import BackgroundSyncBehavior from './BackgroundSyncBehavior';
import StaticResourceCachingBehavior from './StaticResourceCachingStrategy';

class CachingStrategies {

    constructor(staticResourceCaches) {
        this.fastestCachingBehavior = new FastestCachingBehavior(CachingStrategies.options.cache.name);
        this.backgroundSyncBehavior = new BackgroundSyncBehavior(CachingStrategies.options.cache.name);
        this.staticResourceCachingBehavior = new StaticResourceCachingBehavior(staticResourceCaches);
    }

    fastest(request) {
        return this.fastestCachingBehavior.strategy(request);
    }

    backgroundSync(request) {
        return this.backgroundSyncBehavior.strategy(request);
    }

    staticResource(request) {
        return this.staticResourceCachingBehavior.strategy(request);
    }
}

CachingStrategies.options = {
    cache: {
        name: 'shopping-list-dynamic-data-cache'
    }
};

export default CachingStrategies;