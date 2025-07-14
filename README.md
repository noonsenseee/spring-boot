Course Search API – Spring Boot + Elasticsearch
A powerful REST API built with Spring Boot 3 and Elasticsearch 8 for searching, indexing, and auto-suggesting educational courses.

🚀 Features
✅ Full-text search across title & description

🔍 Fuzzy search (handles typos like "jav" → "Java Programming")

💡 Autocomplete using Elasticsearch completion suggester

📅 Search by category, minimum rating, and upcoming session date

📖 Swagger (OpenAPI) documentation

⚡ Fast and scalable Elasticsearch integration with Java Client

📦 Tech Stack
Spring Boot 3.1

Elasticsearch 8.x (via Docker)

Java 17

Springdoc OpenAPI (Swagger UI)

Maven (build tool)

REST APIs (JSON over HTTP)

Setup Instructions
1. 🔄 Run Elasticsearch
Make sure Elasticsearch is running on localhost:9200. You can start it using Docker: docker run -d --name elasticsearch \
  -p 9200:9200 -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  elasticsearch:8.12.2

  Build and Run
  ./mvnw clean install
./mvnw spring-boot:run

API Endpoints
 Health Check
 GET /courses/health

Initialize Sample Courses
POST /courses/init

Fuzzy Search Demo
GET /courses/search?keyword=javv

Swagger Documentation
http://localhost:8081/swagger-ui.html

Developer Notes
Reindexing is required if you change the mapping (suggest field).

You can tweak fuzziness, max suggestions, analyzers inside the service.



