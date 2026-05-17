package ro.semanticweb.bookhomework.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.semanticweb.bookhomework.model.ChatRequest;
import ro.semanticweb.bookhomework.model.ChatResponse;
import ro.semanticweb.bookhomework.service.ChatService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.answer(request);
    }

    @PostMapping("/index/rebuild")
    public ResponseEntity<Map<String, Object>> rebuildIndex() {
        boolean indexed = chatService.rebuildIndex();
        return ResponseEntity.ok(Map.of(
                "indexed", indexed,
                "message", indexed
                        ? "RDF data was indexed into Elasticsearch."
                        : "Elasticsearch is not reachable. Start Docker Compose and try again."
        ));
    }
}
