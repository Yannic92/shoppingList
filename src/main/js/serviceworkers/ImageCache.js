import StaticResourceCachingBehavior from '../api/cache/strategy/StaticResourceCachingBehavior';

const IMAGE_CACHE_VERSION = '2';
const IMAGES_TO_CACHE = [
    '/img/fav.ico',
    '/img/icons/platform/github.svg',
    '/img/icons/action/ic_add_shopping_cart_24px.svg',
    '/img/icons/action/ic_lock_outline_24px.svg',
    '/img/icons/action/ic_shopping_cart_24px.svg',
    '/img/icons/action/ic_delete_24px.svg',
    '/img/icons/action/ic_settings_24px.svg',
    '/img/icons/action/ic_list_24px.svg',
    '/img/icons/action/ic_search_24px.svg',
    '/img/icons/action/ic_chrome_reader_mode_24px.svg',
    '/img/icons/communication/ic_email_24px.svg',
    '/img/icons/communication/ic_vpn_key_24px.svg',
    '/img/icons/communication/ic_clear_all_24px.svg',
    '/img/icons/content/ic_remove_circle_24px.svg',
    '/img/icons/content/ic_save_24px.svg',
    '/img/icons/hardware/ic_keyboard_arrow_left_24px.svg',
    '/img/icons/navigation/ic_menu_24px.svg',
    '/img/icons/navigation/ic_more_vert_24px.svg',
    '/img/icons/navigation/ic_refresh_24px.svg',
    '/img/icons/navigation/ic_close_24px.svg',
    '/img/icons/navigation/ic_arrow_back_24px.svg',
    '/img/icons/notification/ic_sync_24px.svg',
    '/img/icons/notification/ic_sync_disabled_24px.svg',
    '/img/icons/notification/ic_sync_problem_24px.svg',
    '/img/icons/social/ic_mood_bad_48px.svg',
    '/img/icons/social/ic_person_24px.svg',
    '/img/icons/social/ic_person_add_24px.svg',
    '/img/icons/Toggle/ic_check_box_24px.svg',
    '/img/icons/Toggle/ic_check_box_outline_blank_24px.svg'
];

export default new StaticResourceCachingBehavior(self, 'shopping-list-static-images-cache', IMAGE_CACHE_VERSION, IMAGES_TO_CACHE);