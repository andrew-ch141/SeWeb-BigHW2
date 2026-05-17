package ro.semanticweb.bookhomework.service;

import org.springframework.stereotype.Service;
import ro.semanticweb.bookhomework.model.ChatRequest;
import ro.semanticweb.bookhomework.model.ChatResponse;
import ro.semanticweb.bookhomework.model.RagDocument;

import java.util.List;

@Service
public class ChatService {

    private final RagDocumentService ragDocumentService;
    private final ElasticsearchVectorService vectorService;
    private final OpenAiCompatibleClient llmClient;

    public ChatService(
            RagDocumentService ragDocumentService,
            ElasticsearchVectorService vectorService,
            OpenAiCompatibleClient llmClient
    ) {
        this.ragDocumentService = ragDocumentService;
        this.vectorService = vectorService;
        this.llmClient = llmClient;
    }

    public ChatResponse answer(ChatRequest request) {
        List<RagDocument> documents = vectorService.search(enrichQuestion(request), 5);
        if (documents.isEmpty()) {
            vectorService.rebuildIndex(ragDocumentService.buildDocuments());
            documents = vectorService.search(enrichQuestion(request), 5);
        }

        List<String> facts = documents.stream().map(RagDocument::content).toList();
        String answer = llmClient.chat(request.message(), facts);
        List<String> sources = documents.stream().map(RagDocument::title).toList();
        return new ChatResponse(answer, sources);
    }

    public boolean rebuildIndex() {
        return vectorService.rebuildIndex(ragDocumentService.buildDocuments());
    }

    private String enrichQuestion(ChatRequest request) {
        if (request.bookId() == null || request.bookId().isBlank()) {
            return request.message();
        }
        return request.message() + " Current book id: " + request.bookId();
    }
}
