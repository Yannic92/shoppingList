import ArticleController from './ArticleController';

export default class ArticleComponent {

    constructor() {
        this.templateUrl = '/templates/article/article.html';
        this.controller = ArticleController;
        this.controllerAs = 'ctrl';
        this.bindings = {
            article: '='
        };
    }
}