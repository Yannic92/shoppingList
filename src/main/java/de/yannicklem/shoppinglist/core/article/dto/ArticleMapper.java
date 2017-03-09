package de.yannicklem.shoppinglist.core.article.dto;

import de.yannicklem.restutils.entity.dto.RestEntityMapper;
import de.yannicklem.shoppinglist.core.article.Article;
import org.springframework.stereotype.Service;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Service
public class ArticleMapper implements RestEntityMapper<ArticleDto, Article> {

    @Override
    public ArticleDto toDto(Article article) {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setName(article.getName());
        articleDto.setPriceInEuro(article.getPriceInEuro());
        return articleDto;
    }

    @Override
    public Article toEntity(ArticleDto dto) {
        Article article = new Article();
        article.setName(dto.getName());
        article.setPriceInEuro(dto.getPriceInEuro());
        article.setEntityId(dto.getEntityId());
        return article;
    }
}
