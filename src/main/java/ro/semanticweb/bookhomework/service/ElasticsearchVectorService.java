package ro.semanticweb.bookhomework.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ro.semanticweb.bookhomework.config.AppProperties;
import ro.semanticweb.bookhomework.model.RagDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchVectorService {

    private final AppProperties properties;
    private final RestClient restClient;
    private final OpenAiCompatibleClient llmClient;

    public ElasticsearchVectorService(
            AppProperties properties,
            RestClient.Builder builder,
            OpenAiCompatibleClient llmClient
    ) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.elasticsearch().url()).build();
        this.llmClient = llmClient;
    }

    public boolean rebuildIndex(List<RagDocument> documents) {
        try {
            restClient.delete().uri("/{index}", index()).retrieve().toBodilessEntity();
        } catch (RestClientException ignored) {
        }

        try {
            createIndex();
            for (RagDocument document : documents) {
                indexDocument(document);
            }
            return true;
        } catch (RestClientException exception) {
            return false;
        }
    }

    public List<RagDocument> search(String query, int size) {
        List<Double> queryVector = llmClient.embedding(query);
        Map<String, Object> body = Map.of(
                "knn", Map.of(
                        "field", "embedding",
                        "query_vector", queryVector,
                        "k", size,
                        "num_candidates", Math.max(10, size * 5)
                ),
                "_source", List.of("id", "type", "title", "content")
        );

        try {
            JsonNode response = restClient.post()
                    .uri("/{index}/_search", index())
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            List<RagDocument> documents = new ArrayList<>();
            response.path("hits").path("hits").forEach(hit -> {
                JsonNode source = hit.path("_source");
                documents.add(new RagDocument(
                        source.path("id").asText(),
                        source.path("type").asText(),
                        source.path("title").asText(),
                        source.path("content").asText()
                ));
            });
            return documents;
        } catch (RestClientException exception) {
            return List.of();
        }
    }

    private void createIndex() {
        Map<String, Object> body = Map.of(
                "mappings", Map.of(
                        "properties", Map.of(
                                "id", Map.of("type", "keyword"),
                                "type", Map.of("type", "keyword"),
                                "title", Map.of("type", "text"),
                                "content", Map.of("type", "text"),
                                "embedding", Map.of(
                                        "type", "dense_vector",
                                        "dims", properties.llm().embeddingDimensions(),
                                        "index", true,
                                        "similarity", "cosine"
                                )
                        )
                )
        );
        restClient.put().uri("/{index}", index()).body(body).retrieve().toBodilessEntity();
    }

    private void indexDocument(RagDocument document) {
        Map<String, Object> body = Map.of(
                "id", document.id(),
                "type", document.type(),
                "title", document.title(),
                "content", document.content(),
                "embedding", llmClient.embedding(document.content())
        );
        restClient.put()
                .uri("/{index}/_doc/{id}", index(), document.id())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private String index() {
        return properties.elasticsearch().index();
    }
}
