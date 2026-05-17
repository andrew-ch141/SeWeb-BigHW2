package ro.semanticweb.bookhomework.model;

import java.util.List;

public record ChatResponse(String answer, List<String> sources) {
}
