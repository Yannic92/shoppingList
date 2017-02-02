if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/ServiceWorkers.js', {scope: '/'}).then(function(registration) {
            console.debug('ServiceWorker(Cache) registration successful with scope: ', registration.scope);
        }).catch(function(err) {
            console.debug('ServiceWorker(Cache) registration failed: ', err);
        });
    });
}