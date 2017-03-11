import StaticResourceCachingBehavior from '../api/cache/strategy/StaticResourceCachingBehavior';

const APPLICATION_SOURCE_CACHE_VERSION = '8';
const SOURCES_TO_CACHE = [
    '/js/ShoppingList.min.js'
];

export default new StaticResourceCachingBehavior(self, 'shopping-list-static-sources-cache', APPLICATION_SOURCE_CACHE_VERSION, SOURCES_TO_CACHE);
