import AttributeEqualsFilter from '../AttributeEqualsFilter';

export default class ArticleService {

    /*@ngInject*/
    constructor(articleRestService, itemService, $q) {

        this.restService = articleRestService;
        this.itemService = itemService;
        this.articles = this.restService.container;
        this.Promise = $q;
    }

    getAllArticles(refetch = false) {
        if (refetch || this.articlesAlreadyFetched()) {
            this.restService.fetch();
        }
        return this.articles;
    }

    articlesAlreadyFetched() {
        return !this.articles.fetching && !this.articles.fetched;
    }

    findArticleByName(articleName) {
        return AttributeEqualsFilter.findFirstByMatchingAttribute(this.articles, 'name', articleName).element;
    }

    createArticle(article) {

        try {
            const persistedArticleWithSameName = this.findArticleByName(article.name);
            article.entityId = persistedArticleWithSameName.entityId;
            article._links = persistedArticleWithSameName._links;
            return this.Promise.resolve(article);
        } catch (notFoundError) {
            return this.restService.create(article);
        }
    }

    deleteArticle(article) {
        const items  = this.itemService.getAllItems();
        return items.promise.then(() => {
            if(this._isArticleInUseInItems(article, items)) {
                return this.Promise.reject('Article is in use');
            }else {
                return this.restService.delete(article);
            }
        });
    }

    _isArticleInUseInItems(article, items) {
        for(let index = 0 ; index < items.length; index++) {
            if(items[index].article.entityId === article.entityId) {
                return true;
            }
        }
        return false;
    }

    deleteUnusedArticles() {

        const promises = [];
        const items  = this.itemService.getAllItems();
        return items.promise.then(() => {
            this.articles.forEach(article => {
                if (!this._isArticleInUseInItems(article, items)) {
                    promises.push(this.deleteArticle(article));
                }
            });
        }).then(() => this.Promise.all(promises));
    }
}