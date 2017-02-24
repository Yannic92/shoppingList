import StaticResourceCachingBehavior from '../api/cache/strategy/StaticResourceCachingBehavior';

const FONT_CACHE_VERSION = '1';
const FONTS_TO_CACHE = [
    '/style/fonts/roboto_100_italic_latin-ext.woff2',
    '/style/fonts/roboto_300_italic_latin-ext.woff2',
    '/style/fonts/roboto_400_italic_latin-ext.woff2',
    '/style/fonts/roboto_500_italic_latin-ext.woff2',
    '/style/fonts/roboto_700_italic_latin-ext.woff2',
    '/style/fonts/roboto_900_italic_latin-ext.woff2',
    '/style/fonts/roboto_100_italic_latin.woff2',
    '/style/fonts/roboto_300_italic_latin.woff2',
    '/style/fonts/roboto_400_italic_latin.woff2',
    '/style/fonts/roboto_500_italic_latin.woff2',
    '/style/fonts/roboto_700_italic_latin.woff2',
    '/style/fonts/roboto_900_italic_latin.woff2',
    '/style/fonts/roboto_100_normal_latin-ext.woff2',
    '/style/fonts/roboto_300_normal_latin-ext.woff2',
    '/style/fonts/roboto_400_normal_latin-ext.woff2',
    '/style/fonts/roboto_500_normal_latin-ext.woff2',
    '/style/fonts/roboto_700_normal_latin-ext.woff2',
    '/style/fonts/roboto_900_normal_latin-ext.woff2',
    '/style/fonts/roboto_100_normal_latin.woff2',
    '/style/fonts/roboto_300_normal_latin.woff2',
    '/style/fonts/roboto_400_normal_latin.woff2',
    '/style/fonts/roboto_500_normal_latin.woff2',
    '/style/fonts/roboto_700_normal_latin.woff2',
    '/style/fonts/roboto_900_normal_latin.woff2'
];

export default new StaticResourceCachingBehavior(self, 'shopping-list-static-font-cache', FONT_CACHE_VERSION, FONTS_TO_CACHE);
