import FastestCachingBehavior from './FastestCachingBehavior';

class CachingStrategies {

    static fastest(request) {
        return new FastestCachingBehavior(CachingStrategies.options.cache.name).strategy(request);
    }
}

CachingStrategies.options = {
    cache: {
        name: 'shopping-list-dynamic-data-cache'
    }
};

export default CachingStrategies;