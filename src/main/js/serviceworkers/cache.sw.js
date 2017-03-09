import StaticResourceCache from '../api/cache/strategy/StaticResourceCachingBehavior';
import toolbox from 'sw-toolbox';
import CachingStrategies from '../api/cache/strategy/CachingStrategies';
import FontCache from './FontsCache';
import ApplicationSourcesCache from './ApplicationSourcesCache';
import ImageCache from './ImageCache';
import TemplateCache from './TemplateCache';

new StaticResourceCache(self, 'shopping-list-cache', '36', []);

toolbox.options.cache.name = 'shopping-list-dynamic-data-cache';
toolbox.options.networkTimeoutSeconds = 5;
const cachingStrategies = new CachingStrategies([FontCache, ApplicationSourcesCache, ImageCache, TemplateCache]);

const handleUpdateDataRequests = (request) => {
    return cachingStrategies.backgroundSync(request);
};

const handleCreateDataRequests = (request) => {
    return cachingStrategies.backgroundSync(request);
};

const handleDeleteDataRequests = (request) => {
    return cachingStrategies.backgroundSync(request);
};

const handleApiGetRequests = (request) => {
    return cachingStrategies.networkOnly(request);
};

const handleStaticResourceRequest = (request) => {
    return cachingStrategies.staticResource(request);
};

toolbox.router.get(/^(?!\/?api).+$/, handleStaticResourceRequest);
toolbox.router.get('/api/(.*)', handleApiGetRequests);
toolbox.router.put('/api/(.*)', handleUpdateDataRequests);
toolbox.router.post('/api/(.*)', handleCreateDataRequests);
toolbox.router.delete('/api/(.*)', handleDeleteDataRequests);


