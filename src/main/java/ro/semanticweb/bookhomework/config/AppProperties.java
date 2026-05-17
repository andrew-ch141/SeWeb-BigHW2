package ro.semanticweb.bookhomework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Rdf rdf,
        Elasticsearch elasticsearch,
        Llm llm
) {
    public record Rdf(String path, String baseUri) {
    }

    public record Elasticsearch(String url, String index) {
    }

    public record Llm(
            String baseUrl,
            String apiKey,
            String chatModel,
            String embeddingModel,
            int embeddingDimensions
    ) {
    }
}
