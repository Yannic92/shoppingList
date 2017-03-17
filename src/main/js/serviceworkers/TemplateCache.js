import StaticResourceCachingBehavior from '../api/cache/strategy/StaticResourceCachingBehavior';

const TEMPLATE_CACHE_VERSION = '4';
const TEMPLATES_TO_CACHE = [
    '/',
    '/index.html',
    '/templates/authentication/login/login.html',
    '/templates/navigation/navigation.html',
    '/templates/navigation/navMenu/navMenu.html',
    '/templates/navigation/leftSideNav/leftSideNav.html',
    '/templates/user/delete/userDelete.html',
    '/templates/user/edit/userData.html',
    '/templates/lists/list.html',
    '/templates/lists/lists.html',
    '/templates/lists/view/overview.html',
    '/templates/lists/view/view.html',
    '/templates/lists/view/emptyList.html',
    '/templates/lists/new/newList.html',
    '/templates/lists/edit/editList.html',
    '/templates/directives/loadingCycle.html',
    '/templates/authentication/register/register.html',
    '/templates/authentication/register/confirmation/confirmationNotification.html',
    '/templates/authentication/register/confirmation/confirmation.html',
    '/templates/item/new/newItem.html',
    '/templates/item/item.html',
    '/templates/article/article.html',
    '/templates/article/dictionary/dictionary.html',
    '/templates/article/dictionary/emptyDictionary.html',
    '/manifest.json',
    '/style/css/app-compact.css'
];

export default new StaticResourceCachingBehavior(self, 'shopping-list-static-templates-cache', TEMPLATE_CACHE_VERSION, TEMPLATES_TO_CACHE);