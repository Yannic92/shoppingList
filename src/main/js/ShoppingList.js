import angular from 'angular';
import ngResource from 'angular-resource';
import ngMaterial from 'angular-material';
import ngAnimate from 'angular-animate';
import ngAria from 'angular-aria';
import ngMessages from 'angular-messages';
import UserService from './user/service/UserService';
import UserDataController from './user/edit/UserDataController';
import DeleteUserController from './user/delete/DeleteUserController';
import NavigationController from './navigation/NavigationController';
import NavMenuController from './navigation/navMenu/NavMenuController';
import LeftSideNavController from './navigation/leftSideNav/LeftSideNavController';
import ListsController from './lists/ListsController';
import ListController from './lists/ListController';
import ListViewController from './lists/view/ListViewController';
import ListService from './lists/service/ListService';
import NewListController from './lists/new/NewListController';
import EditListController from './lists/edit/EditListController';
import ItemController from './item/ItemController';
import ItemService from './item/service/ItemService';
import OnLongPressDirective from './directives/OnLongPressDirective';
import LoadingCycle from './directives/LoadingCycle';
import FocusMeDirective from './directives/FocusMeDirective';
import CompareToDirective from './directives/CompareToDirective';
import HttpInterceptor from './authentication/HttpInterceptor';
import AuthService from './authentication/AuthService';
import RegisterController from './authentication/register/RegisterController';
import ConfirmationNotificationController from './authentication/register/confirmation/ConfirmationNotificationController';
import ConfirmationController from './authentication/register/confirmation/ConfirmationController';
import LoginController from './authentication/login/LoginController';
import ArticleService from './article/ArticleService';
import ArticleController from './article/ArticleController';
import DictionaryController from './article/dictionary/DictionaryController';

/*global require*/
require('angular-route');

angular.module('shoppingList', ['ngRoute', ngResource, ngMaterial, ngAnimate, ngAria, ngMessages])
    .config(['$routeProvider', '$httpProvider',
        ($routeProvider, $httpProvider) => {
            $routeProvider.otherwise({redirectTo: '/lists'});
            $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
            $httpProvider.interceptors.push('MyHttpInterceptor');
        }
    ])
    .run(['$rootScope', '$location', '$anchorScroll',
        ($rootScope, $location, $anchorScroll) => {
            $rootScope.authenticated = false;
            $rootScope.user = '';
            $rootScope.authenticationAlreadyChecked = false;
            $rootScope.reset = () => {
                $rootScope.options = [];
                $rootScope.title = '';
                $rootScope.shortCutAction = {
                    available: false
                };
                $rootScope.loading = false;
            };
            $rootScope.goToTop = () => {
                $location.hash('top');
                $anchorScroll();
                $location.hash('');

            };
        }
    ])
    .directive('onLongPress', OnLongPressDirective)
    .directive('loadingCycle', LoadingCycle)
    .directive('focusMe', FocusMeDirective)
    .directive('compareTo', CompareToDirective)
    .service('MyHttpInterceptor', HttpInterceptor)
    .service('userService', UserService)
    .service('listService', ListService)
    .service('itemService', ItemService)
    .service('authService', AuthService)
    .service('articleService', ArticleService)
    .controller('navigationController', NavigationController)
    .controller('navMenuController', NavMenuController)
    .controller('leftSideNavController', LeftSideNavController)
    .controller('userDataController', UserDataController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/userData', {
            templateUrl: '/templates/user/edit/userData.html',
            controller: 'userDataController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('deleteUserController', DeleteUserController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/deleteAccount', {
            templateUrl: '/templates/user/delete/userDelete.html',
            controller: 'deleteUserController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('listController', ListController)
    .controller('listsController', ListsController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/lists', {
            templateUrl: '/templates/lists/lists.html',
            controller: 'listsController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('listViewController', ListViewController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/lists/:listId', {
            templateUrl: '/templates/lists/view/view.html',
            controller: 'listViewController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('newListController', NewListController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/newList', {
            templateUrl: '/templates/lists/new/newList.html',
            controller: 'newListController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('editListController', EditListController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/lists/:id/edit', {
            templateUrl: '/templates/lists/edit/editList.html',
            controller: 'editListController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('itemController', ItemController)
    .controller('registerController', RegisterController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/register', {
            templateUrl: '/templates/authentication/register/register.html',
            controller: 'registerController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('confirmationNotificationController', ConfirmationNotificationController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/register/confirmation', {
            templateUrl: '/templates/authentication/register/confirmation/confirmationNotification.html',
            controller: 'confirmationNotificationController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('confirmationController', ConfirmationController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/register/confirmation/:username/:code', {
            templateUrl: '/templates/authentication/register/confirmation/confirmation.html',
            controller: 'confirmationController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('loginController', LoginController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/login', {
            templateUrl: '/templates/authentication/login/login.html',
            controller: 'loginController',
            controllerAs: 'ctrl'
        }).when('/login/:historyRoot', {
            templateUrl: '/templates/authentication/login/login.html',
            controller: 'loginController',
            controllerAs: 'ctrl'
        });
    }])
    .controller('articleController', ArticleController)
    .controller('dictionaryController', DictionaryController)
    .config(['$routeProvider', ($routeProvider) => {
        $routeProvider.when('/dictionary', {
            templateUrl: '/templates/article/dictionary/dictionary.html',
            controller: 'dictionaryController'
        });
    }]);
