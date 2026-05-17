package ro.semanticweb.bookhomework.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank String message,
        String pageType,
        String bookId
) {
}
