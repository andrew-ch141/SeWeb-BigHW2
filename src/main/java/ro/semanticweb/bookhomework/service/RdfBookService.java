package ro.semanticweb.bookhomework.service;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ro.semanticweb.bookhomework.config.AppProperties;
import ro.semanticweb.bookhomework.model.Book;
import ro.semanticweb.bookhomework.model.TripleView;
import ro.semanticweb.bookhomework.model.UserProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RdfBookService {

    private final AppProperties properties;

    public RdfBookService(AppProperties properties) {
        this.properties = properties;
    }

    public synchronized List<Book> findAllBooks() {
        Model model = loadModel();
        Resource bookType = model.createResource(ns() + "Book");

        return model.listResourcesWithProperty(RDF.type, bookType)
                .mapWith(resource -> toBook(model, resource))
                .toList()
                .stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .toList();
    }

    public synchronized Optional<Book> findBook(String id) {
        Model model = loadModel();
        Resource resource = model.getResource(ns() + id);
        if (!model.contains(resource, RDF.type, model.createResource(ns() + "Book"))) {
            return Optional.empty();
        }
        return Optional.of(toBook(model, resource));
    }

    public synchronized void saveBook(Book book) {
        Model model = loadModel();
        Resource resource = model.createResource(ns() + cleanId(book.getId(), book.getTitle()));

        resource.removeAll(RDF.type);
        resource.removeAll(RDFS.label);
        resource.removeAll(prop("author"));
        resource.removeAll(prop("readingLevel"));
        resource.removeAll(prop("theme"));

        resource.addProperty(RDF.type, model.createResource(ns() + "Book"));
        resource.addProperty(RDFS.label, book.getTitle());
        resource.addProperty(prop("author"), book.getAuthor());
        resource.addProperty(prop("readingLevel"), book.getReadingLevel());
        book.getThemes().stream()
                .map(String::trim)
                .filter(theme -> !theme.isBlank())
                .forEach(theme -> resource.addProperty(prop("theme"), theme));

        writeModel(model);
    }

    public synchronized List<UserProfile> findAllUsers() {
        Model model = loadModel();
        Resource userType = model.createResource(ns() + "User");

        return model.listResourcesWithProperty(RDF.type, userType)
                .mapWith(resource -> toUser(model, resource))
                .toList();
    }

    public synchronized List<TripleView> triplesFromCurrentFile() {
        return toTriples(loadModel());
    }

    public List<TripleView> triplesFromUpload(MultipartFile file) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        try (InputStream input = file.getInputStream()) {
            model.read(input, properties.rdf().baseUri());
        }
        return toTriples(model);
    }

    public synchronized void ensureDefaultHarryPotter() {
        if (findBook("HarryPotter").isPresent()) {
            return;
        }
        saveBook(new Book(
                "HarryPotter",
                "Harry Potter",
                "J. K. Rowling",
                "Beginner",
                List.of("Fantasy", "Magic")
        ));
    }

    public String cleanId(String id, String fallbackTitle) {
        String raw = id == null || id.isBlank() ? fallbackTitle : id;
        String cleaned = raw.replaceAll("[^A-Za-z0-9]", "");
        return cleaned.isBlank() ? "Book" + System.currentTimeMillis() : cleaned;
    }

    private Model loadModel() {
        Model model = ModelFactory.createDefaultModel();
        Path path = Path.of(properties.rdf().path());
        if (!Files.exists(path)) {
            return model;
        }
        try (InputStream input = Files.newInputStream(path)) {
            model.read(input, properties.rdf().baseUri());
            return model;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read RDF file: " + path, exception);
        }
    }

    private void writeModel(Model model) {
        Path path = Path.of(properties.rdf().path());
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream output = Files.newOutputStream(path)) {
                model.write(output, "RDF/XML-ABBREV");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not write RDF file: " + path, exception);
        }
    }

    private Book toBook(Model model, Resource resource) {
        List<String> themes = model.listObjectsOfProperty(resource, prop("theme"))
                .mapWith(this::literalValue)
                .toList();

        return new Book(
                resource.getLocalName(),
                literalValue(resource.getProperty(RDFS.label).getObject()),
                literalOrEmpty(resource, prop("author")),
                literalOrEmpty(resource, prop("readingLevel")),
                themes
        );
    }

    private UserProfile toUser(Model model, Resource resource) {
        List<String> preferredThemes = model.listObjectsOfProperty(resource, prop("prefersTheme"))
                .mapWith(this::literalValue)
                .toList();

        return new UserProfile(
                resource.getLocalName(),
                literalValue(resource.getProperty(RDFS.label).getObject()),
                literalOrEmpty(resource, prop("readingLevel")),
                preferredThemes
        );
    }

    private List<TripleView> toTriples(Model model) {
        List<TripleView> triples = new ArrayList<>();
        model.listStatements().forEachRemaining(statement -> triples.add(new TripleView(
                shortName(statement.getSubject()),
                shortName(statement.getPredicate()),
                statement.getObject().isLiteral()
                        ? statement.getObject().asLiteral().getString()
                        : shortName(statement.getObject())
        )));
        return triples;
    }

    private String literalOrEmpty(Resource resource, Property property) {
        if (resource.getProperty(property) == null) {
            return "";
        }
        return literalValue(resource.getProperty(property).getObject());
    }

    private String literalValue(RDFNode node) {
        if (node == null) {
            return "";
        }
        if (node.isLiteral()) {
            Literal literal = node.asLiteral();
            return literal.getString();
        }
        return shortName(node);
    }

    private String shortName(RDFNode node) {
        return node.isResource() ? shortName(node.asResource()) : node.toString();
    }

    private String shortName(Resource resource) {
        return resource.getLocalName() == null ? resource.getURI() : resource.getLocalName();
    }

    private String shortName(Property property) {
        return property.getLocalName() == null ? property.getURI() : property.getLocalName();
    }

    private Property prop(String localName) {
        return ModelFactory.createDefaultModel().createProperty(ns() + localName);
    }

    private String ns() {
        return properties.rdf().baseUri();
    }
}
