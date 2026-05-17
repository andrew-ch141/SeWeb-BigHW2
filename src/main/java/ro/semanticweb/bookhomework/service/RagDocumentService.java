package ro.semanticweb.bookhomework.service;

import org.springframework.stereotype.Service;
import ro.semanticweb.bookhomework.model.Book;
import ro.semanticweb.bookhomework.model.RagDocument;
import ro.semanticweb.bookhomework.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagDocumentService {

    private final RdfBookService rdfBookService;

    public RagDocumentService(RdfBookService rdfBookService) {
        this.rdfBookService = rdfBookService;
    }

    public List<RagDocument> buildDocuments() {
        List<RagDocument> documents = new ArrayList<>();
        rdfBookService.findAllBooks().forEach(book -> documents.add(bookDocument(book)));
        rdfBookService.findAllUsers().forEach(user -> documents.add(userDocument(user)));
        return documents;
    }

    private RagDocument bookDocument(Book book) {
        String content = """
                Book title: %s.
                Author: %s.
                Themes: %s.
                Reading level: %s.
                Use this source when answering questions about this book, author, theme, or reading level.
                """.formatted(
                book.getTitle(),
                book.getAuthor(),
                String.join(", ", book.getThemes()),
                book.getReadingLevel()
        );
        return new RagDocument("book-" + book.getId(), "book", book.getTitle(), content);
    }

    private RagDocument userDocument(UserProfile user) {
        String content = """
                User name: %s.
                Reading level: %s.
                Preferred themes: %s.
                A recommended book should match the user's preferred theme and reading level.
                """.formatted(
                user.getName(),
                user.getReadingLevel(),
                String.join(", ", user.getPreferredThemes())
        );
        return new RagDocument("user-" + user.getId(), "user", user.getName(), content);
    }
}
