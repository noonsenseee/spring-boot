package com.example.coursesearch.document;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDocument {
    private String id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String gradeRange;
    private int minAge;
    private int maxAge;
    private double price;
    private Instant nextSessionDate;

    
    private SuggestInput suggest;

    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestInput {
        private List<String> input;
    }
}
