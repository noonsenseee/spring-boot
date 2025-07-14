package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.json.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    
    public List<CourseDocument> searchCourses(String keyword, String category, Double minRating, Instant startDate) throws IOException {
        List<Query> queries = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            queries.add(Query.of(q -> q
                .multiMatch(m -> m
                    .fields("title", "description")
                    .query(keyword)
                    .fuzziness("AUTO")  
                )
            ));
        }

        if (category != null && !category.isEmpty()) {
            queries.add(Query.of(q -> q
                .term(t -> t
                    .field("category.keyword")
                    .value(category)
                )
            ));
        }

        if (minRating != null) {
            queries.add(Query.of(q -> q
                .range(r -> r
                    .field("rating")
                    .gte(JsonData.of(minRating))
                )
            ));
        }

        if (startDate != null) {
            queries.add(Query.of(q -> q
                .range(r -> r
                    .field("nextSessionDate")
                    .gte(JsonData.of(startDate.toString()))
                )
            ));
        }

        Query finalQuery = queries.isEmpty()
                ? Query.of(q -> q.matchAll(m -> m))
                : Query.of(q -> q.bool(b -> b.must(queries)));

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("courses")
            .query(finalQuery)
            .size(50)
        );

        SearchResponse<CourseDocument> response = elasticsearchClient.search(searchRequest, CourseDocument.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }

    
    public List<CourseDocument> searchByKeyword(String keyword) throws IOException {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("courses")
            .query(q -> q
                .multiMatch(m -> m
                    .fields("title", "description")
                    .query(keyword)
                    .fuzziness("AUTO")  
                )
            )
            .size(50)
        );

        SearchResponse<CourseDocument> response = elasticsearchClient.search(searchRequest, CourseDocument.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }

    
    public void saveAll(List<CourseDocument> courses) throws IOException {
        for (CourseDocument course : courses) {
            IndexRequest<CourseDocument> request = IndexRequest.of(i -> i
                .index("courses")
                .id(course.getId())
                .document(course)
            );
            elasticsearchClient.index(request);
        }
    }

    
    public List<String> getAutocompleteSuggestions(String query) {
        try {
            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                .index("courses")
                .suggest(sg -> sg
                    .suggesters("course-suggest", sg1 -> sg1
                        .prefix(query)
                        .completion(c -> c
                            .field("suggest")
                            .size(10)
                        )
                    )
                ),
                Void.class
            );

            if (response.suggest() == null || response.suggest().get("course-suggest") == null) {
                return List.of();
            }

            List<Suggestion<Void>> suggestions = response.suggest().get("course-suggest");
            List<String> results = new ArrayList<>();

            for (Suggestion<Void> suggestion : suggestions) {
                List<CompletionSuggestOption<Void>> options = suggestion.completion().options();
                for (CompletionSuggestOption<Void> option : options) {
                    results.add(option.text());
                }
            }

            return results;
        } catch (Exception e) {
            System.err.println("❌ Error fetching suggestions: " + e.getMessage());
            return List.of();
        }
    }
}
