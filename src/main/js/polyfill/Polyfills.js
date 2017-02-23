import BroadcastChannelPolyFill from './BroadcastChannelPolyFill';

export default class Polyfills {

    /* global BroadcastChannel */

    static initPolyfills() {
        if(typeof BroadcastChannel !== 'function') {
            BroadcastChannelPolyFill(self);
        }
    }
}