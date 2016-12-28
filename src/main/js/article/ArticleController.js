import ResponsiveListItemController from '../global/ResponsiveListItemController';

export default class ArticleController extends ResponsiveListItemController{

    /*@ngInject*/
    constructor($scope, articleService, $timeout) {

        super($scope, $timeout);

        this.articleService = articleService;
    }

    deleteArticle() {

        return this.articleService.delete(this.article);
    }
}