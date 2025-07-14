package com.example.coursesearch.controller;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseSearchService searchService;

    @GetMapping("/search")
    public List<CourseDocument> search(@RequestParam String keyword) throws IOException {
       
        return searchService.searchCourses(keyword, null, null, null);
    }

    @GetMapping("/")
    public String home() {
        return "✅ Course Search API is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "✅ Healthy!";
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam("q") String query) {
        return searchService.getAutocompleteSuggestions(query);
    }


    @PostMapping("/init")
    public String init() throws IOException {
        List<CourseDocument> courses = List.of(
            new CourseDocument(
                UUID.randomUUID().toString(),
                "Java Programming",
                "Learn Java from scratch",
                "Programming",
                "Online",
                "8-12",
                8,
                12,
                199.99,
                Instant.now().plusSeconds(86400),
                new CourseDocument.SuggestInput(List.of("Java Programming"))
            ),
            new CourseDocument(
                UUID.randomUUID().toString(),
                "Spring Boot Fundamentals",
                "Master backend development with Spring Boot",
                "Backend",
                "Online",
                "13-18",
                13,
                18,
                249.99,
                Instant.now().plusSeconds(172800),
                new CourseDocument.SuggestInput(List.of("Spring Boot Fundamentals"))
            ),
            new CourseDocument(
                UUID.randomUUID().toString(),
                "Data Structures in Java",
                "Deep dive into data structures with Java",
                "Programming",
                "Offline",
                "15-20",
                15,
                20,
                299.99,
                Instant.now().plusSeconds(259200),
                new CourseDocument.SuggestInput(List.of("Data Structures in Java"))
            )
        );

        searchService.saveAll(courses); 
        return "✅ Sample courses indexed successfully!";
    }
}
