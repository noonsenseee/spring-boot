package com.example.coursesearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.coursesearch.document.CourseDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseIndexerService {

    private final ElasticsearchClient client;

    @PostConstruct
    public void loadSampleCourses() {
        System.out.println("🔍 DEBUG: CourseIndexerService @PostConstruct triggered");

        try {
           
            boolean exists = client.indices().exists(e -> e.index("courses")).value();

            if (!exists) {
                
                client.indices().create(c -> c
                    .index("courses")
                    .mappings(m -> m
                        .properties("id", p -> p.keyword(k -> k))
                        .properties("title", p -> p.text(t -> t))
                        .properties("description", p -> p.text(t -> t))
                        .properties("topic", p -> p.keyword(k -> k))
                        .properties("level", p -> p.keyword(k -> k))
                        .properties("price", p -> p.float_(f -> f))
                        .properties("rating", p -> p.float_(f -> f))
                        .properties("suggest", p -> p.completion(cmp -> cmp))
                    )
                );
                System.out.println("✅ Index 'courses' created with suggest mapping");
            } else {
                System.out.println("⚠️ Index 'courses' already exists");
            }

            
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            if (!resource.exists()) {
                System.err.println("❌ sample-courses.json not found in classpath!");
                return;
            }

            InputStream inputStream = resource.getInputStream();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            List<CourseDocument> courses = mapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});

            if (courses == null || courses.isEmpty()) {
                System.err.println("❌ JSON parsed but no courses found.");
                return;
            }

            for (CourseDocument course : courses) {
              
                course.setSuggest(new CourseDocument.SuggestInput(List.of(course.getTitle())));

                IndexRequest<CourseDocument> request = IndexRequest.of(i -> i
                    .index("courses")
                    .id(course.getId())
                    .document(course)
                );

                IndexResponse response = client.index(request);
                System.out.println("✅ Indexed course ID: " + response.id());
            }

            System.out.println("✅ Successfully indexed " + courses.size() + " courses.");
        } catch (Exception e) {
            System.err.println("❌ Failed to load and index sample courses: " + e.getMessage());
            e.printStackTrace();
        }
    } 
}
