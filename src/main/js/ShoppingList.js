import angular from 'angular';
import ngRoute from 'angular-route';
import ngResource from 'angular-resource';
import ngMaterial from 'angular-material';
import ngAnimate from 'angular-animate';
import ngAria from 'angular-aria';
import ngMessages from 'angular-messages';
import ngTouch from 'angular-touch';
import UserService from './api/user/UserService';
import UserDataController from './user/edit/UserDataController';
import DeleteUserController from './user/delete/DeleteUserController';
import NavigationController from './navigation/NavigationController';
import NavMenuController from './navigation/navMenu/NavMenuController';
import LeftSideNavController from './navigation/leftSideNav/LeftSideNavController';
import ListsController from './lists/ListsController';
import ListController from './lists/ListController';
import ListViewController from './lists/view/ListViewController';
import ListOverviewController from './lists/view/ListOverviewController';
import ListService from './api/list/ListService';
import NewListController from './lists/new/NewListController';
import EditListController from './lists/edit/EditListController';
import ItemService from './api/item/ItemService';
import OnLongPressDirective from './directives/OnLongPressDirective';
import LoadingCycle from './directives/LoadingCycle';
import FocusMeDirective from './directives/FocusMeDirective';
import CompareToDirective from './directives/CompareToDirective';
import HttpInterceptor from './authentication/AuthenticationInterceptor';
import AuthService from './authentication/AuthService';
import RegisterController from './authentication/register/RegisterController';
import ConfirmationNotificationController from './authentication/register/confirmation/ConfirmationNotificationController';
import ConfirmationController from './authentication/register/confirmation/ConfirmationController';
import LoginController from './authentication/login/LoginController';
import ArticleService from './api/article/ArticleService';
import DictionaryController from './article/dictionary/DictionaryController';
import NavigationService from './navigation/NavigationService';
import {UserPropertyFilter} from './lists/owners/UserPropertyFilter';
import {UserAlreadyContainedFilter} from './lists/owners/UserAlreadyContainedFilter';
import ItemComponent from './item/ItemComponent';
import ArticleComponent from './article/ArticleComponent';
import ItemResourceConverter from './api/item/ItemResourceConverter';
import ArticleResourceConverter from './api/article/ArticleResourceConverter';
import ShoppingListResourceConverter from './api/list/ShoppingListResourceConverter';
import UserResourceConverter from './api/user/UserResourceConverter';
import CredentialService from './authentication/CredentialService';
import Polyfills from './polyfill/Polyfills';
import ArticleRestService from './api/article/ArticleRestService';
import ItemRestService from './api/item/ItemRestService';
import ShoppingListRestService from './api/list/ShoppingListRestService';
import UserRestService from './api/user/UserRestService';
import ListItemRestService from './api/item/ListItemRestService';

Polyfills.initPolyfills();

angular.module('shoppingList', [ngRoute, ngResource, ngMaterial, ngAnimate, ngAria, ngMessages, ngTouch])
    .config(/*@ngInject*/($routeProvider, $httpProvider) => {
        $routeProvider.otherwise({redirectTo: '/lists'});
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        $httpProvider.interceptors.push('authenticationInterceptor');
    })
    .run(/*@ngInject*/($rootScope) => {
        $rootScope.authenticated = false;
        $rootScope.user = null;
        $rootScope.authenticationAlreadyChecked = false;
        $rootScope.reset = () => {
            $rootScope.options = [];
            $rootScope.title = '';
            $rootScope.shortCutAction = {
                available: false
            };
            $rootScope.loading = false;
        };
    })
    .directive('onLongPress', OnLongPressDirective)
    .directive('loadingCycle', LoadingCycle)
    .directive('focusMe', FocusMeDirective)
    .directive('compareTo', CompareToDirective)
    .service('credentialService', CredentialService)
    .service('navigationService', NavigationService)
    .service('authenticationInterceptor', HttpInterceptor)
    .service('userRestService', UserRestService)
    .service('userService', UserService)
    .service('shoppingListResourceConverter', ShoppingListResourceConverter)
    .service('shoppingListRestService', ShoppingListRestService)
    .service('listService', ListService)
    .service('itemResourceConverter', ItemResourceConverter)
    .service('itemRestService', ItemRestService)
    .service('listItemRestService', ListItemRestService)
    .service('itemService', ItemService)
    .service('authService', AuthService)
    .service('articleResourceConverter', ArticleResourceConverter)
    .service('articleRestService', ArticleRestService)
    .service('articleService', ArticleService)
    .service('userResourceConverter', UserResourceConverter)
    .controller('navigationController', NavigationController)
    .controller('navMenuController', NavMenuController)
    .controller('leftSideNavController', LeftSideNavController)
    .controller('userDataController', UserDataController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/userData', {
            templateUrl: '/templates/user/edit/userData.html',
            controller: 'userDataController',
            controllerAs: 'ctrl'
        });
    })
    .controller('deleteUserController', DeleteUserController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/deleteAccount', {
            templateUrl: '/templates/user/delete/userDelete.html',
            controller: 'deleteUserController',
            controllerAs: 'ctrl'
        });
    })
    .controller('listController', ListController)
    .controller('listsController', ListsController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/lists', {
            templateUrl: '/templates/lists/lists.html',
            controller: 'listsController',
            controllerAs: 'ctrl'
        });
    })
    .controller('listOverviewController', ListOverviewController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/lists/overview', {
            templateUrl: '/templates/lists/view/overview.html',
            controller: 'listOverviewController',
            controllerAs: 'ctrl'
        });
    })
    .controller('listViewController', ListViewController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/lists/:listId', {
            templateUrl: '/templates/lists/view/view.html',
            controller: 'listViewController',
            controllerAs: 'ctrl'
        });
    })
    .filter('userPropertyFilter', UserPropertyFilter)
    .filter('userAlreadyContainedFilter', UserAlreadyContainedFilter)
    .controller('newListController', NewListController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/newList', {
            templateUrl: '/templates/lists/new/newList.html',
            controller: 'newListController',
            controllerAs: 'ctrl'
        });
    })
    .controller('editListController', EditListController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/lists/:id/edit', {
            templateUrl: '/templates/lists/edit/editList.html',
            controller: 'editListController',
            controllerAs: 'ctrl'
        });
    })
    .component('shoppingListItem', new ItemComponent())
    .controller('registerController', RegisterController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/register', {
            templateUrl: '/templates/authentication/register/register.html',
            controller: 'registerController',
            controllerAs: 'ctrl'
        });
    })
    .controller('confirmationNotificationController', ConfirmationNotificationController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/register/confirmation', {
            templateUrl: '/templates/authentication/register/confirmation/confirmationNotification.html',
            controller: 'confirmationNotificationController',
            controllerAs: 'ctrl'
        });
    })
    .controller('confirmationController', ConfirmationController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/register/confirmation/:username/:code', {
            templateUrl: '/templates/authentication/register/confirmation/confirmation.html',
            controller: 'confirmationController',
            controllerAs: 'ctrl'
        });
    })
    .controller('loginController', LoginController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/login', {
            templateUrl: '/templates/authentication/login/login.html',
            controller: 'loginController',
            controllerAs: 'ctrl'
        }).when('/login/:historyRoot', {
            templateUrl: '/templates/authentication/login/login.html',
            controller: 'loginController',
            controllerAs: 'ctrl'
        });
    })
    .component('shoppingArticle', new ArticleComponent())
    .controller('dictionaryController', DictionaryController)
    .config(/*@ngInject*/($routeProvider) => {
        $routeProvider.when('/dictionary', {
            templateUrl: '/templates/article/dictionary/dictionary.html',
            controller: 'dictionaryController',
            controllerAs: 'ctrl'
        });
    });