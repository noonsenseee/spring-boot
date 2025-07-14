package com.example.coursesearch.controller;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseSearchController {

    @Autowired
    private CourseSearchService courseSearchService;

    @GetMapping("/search")
    public List<CourseDocument> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate
    ) throws IOException {
        return courseSearchService.searchCourses(keyword, category, minRating, startDate);
    }

    
    @GetMapping("/suggest")
    public List<String> suggestCourses(@RequestParam("q") String query) {
        return courseSearchService.getAutocompleteSuggestions(query);
    }
}
