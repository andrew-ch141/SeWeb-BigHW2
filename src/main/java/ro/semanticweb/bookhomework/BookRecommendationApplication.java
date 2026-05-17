package ro.semanticweb.bookhomework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ro.semanticweb.bookhomework.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class BookRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookRecommendationApplication.class, args);
    }
}
