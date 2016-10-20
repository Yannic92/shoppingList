export default class ArticleController {

    constructor($scope, articleService, $timeout) {

        this.articleService = articleService;
        this.$timeout = $timeout;

        this.optionsShown = false;
        this.optionsVisible = false;
        this.optionsVisibleStopped = false;
        this._initOptionsShownListener($scope);
    }

    showOptionsOnElement() {
        if (!this.optionsVisibleStopped) {
            this.optionsVisible = true;
        }
    }

    hideOptionsOnElement() {
        this.optionsVisibleStopped = true;
        this.optionsVisible = false;

        this.$timeout(() => {
            this.optionsVisibleStopped = false;
        }, 15);
    }

    showOptions() {
        this.optionActive = true;

        this.$timeout(() => {
            this.optionsShown = true;
            this.hideOptionsOnElement();
        }, 20);
    }

    hideOptions() {
        this.optionsShown = false;
    }

    deleteArticle(article) {

        return this.articleService.delete(article);
    }

    _initOptionsShownListener($scope) {
        $scope.$watch(() => this.optionsShown, (newValue, oldValue) => {

            if (oldValue && !newValue) {
                this.$timeout(() => {
                    this.optionActive = false;
                    this.hideOptionsOnElement();
                });
            }
        });
    }
}