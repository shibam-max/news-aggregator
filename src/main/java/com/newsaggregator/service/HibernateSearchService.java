package com.newsaggregator.service;

import com.newsaggregator.entity.NewsArticleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HibernateSearchService {
    
    private final EntityManager entityManager;
    
    public List<NewsArticleEntity> searchArticles(String searchTerm, int maxResults) {
        SearchSession searchSession = Search.session(entityManager);
        
        return searchSession.search(NewsArticleEntity.class)
                .where(f -> f.bool()
                        .should(f.match()
                                .field("title")
                                .matching(searchTerm)
                                .boost(2.0f))
                        .should(f.match()
                                .field("description")
                                .matching(searchTerm))
                        .should(f.match()
                                .field("author")
                                .matching(searchTerm)
                                .boost(1.5f))
                )
                .sort(f -> f.composite()
                        .add(f.score().desc())
                        .add(f.field("publishedAt").desc()))
                .fetchHits(maxResults);
    }
    
    public List<NewsArticleEntity> fuzzySearchArticles(String searchTerm, int maxResults) {
        SearchSession searchSession = Search.session(entityManager);
        
        return searchSession.search(NewsArticleEntity.class)
                .where(f -> f.bool()
                        .should(f.match()
                                .field("title")
                                .matching(searchTerm)
                                .fuzzy(2))
                        .should(f.match()
                                .field("description")
                                .matching(searchTerm)
                                .fuzzy(2))
                )
                .sort(f -> f.score().desc())
                .fetchHits(maxResults);
    }
    
    public List<NewsArticleEntity> searchBySourceAndKeyword(String source, String keyword, int maxResults) {
        SearchSession searchSession = Search.session(entityManager);
        
        return searchSession.search(NewsArticleEntity.class)
                .where(f -> f.bool()
                        .must(f.match()
                                .field("source")
                                .matching(source))
                        .must(f.bool()
                                .should(f.match()
                                        .field("title")
                                        .matching(keyword))
                                .should(f.match()
                                        .field("description")
                                        .matching(keyword))
                        )
                )
                .sort(f -> f.field("publishedAt").desc())
                .fetchHits(maxResults);
    }
    
    @Transactional
    public void rebuildSearchIndex() {
        try {
            SearchSession searchSession = Search.session(entityManager);
            searchSession.massIndexer(NewsArticleEntity.class)
                    .threadsToLoadObjects(4)
                    .batchSizeToLoadObjects(25)
                    .startAndWait();
            log.info("Search index rebuilt successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Search index rebuild was interrupted", e);
        }
    }
}