package ro.semanticweb.bookhomework.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.semanticweb.bookhomework.model.Book;
import ro.semanticweb.bookhomework.service.RdfBookService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final RdfBookService rdfBookService;

    public BookController(RdfBookService rdfBookService) {
        this.rdfBookService = rdfBookService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", rdfBookService.findAllBooks());
        model.addAttribute("pageType", "books");
        return "books/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        Book book = rdfBookService.findBook(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        model.addAttribute("pageType", "book");
        model.addAttribute("bookId", id);
        return "books/detail";
    }

    @GetMapping("/new")
    public String addForm(Model model) {
        model.addAttribute("book", new Book("", "", "", "Beginner", List.of()));
        model.addAttribute("themeText", "");
        model.addAttribute("pageType", "book-form");
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        Book book = rdfBookService.findBook(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        model.addAttribute("themeText", String.join(", ", book.getThemes()));
        model.addAttribute("pageType", "book-form");
        return "books/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute Book book,
            BindingResult bindingResult,
            String themeText,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("themeText", themeText);
            return "books/form";
        }
        book.setId(rdfBookService.cleanId(book.getId(), book.getTitle()));
        book.setThemes(splitThemes(themeText));
        rdfBookService.saveBook(book);
        redirectAttributes.addFlashAttribute("message", "Book saved in RDF/XML.");
        return "redirect:/books/" + book.getId();
    }

    @PostMapping("/seed-harry-potter")
    public String seedHarryPotter(RedirectAttributes redirectAttributes) {
        rdfBookService.ensureDefaultHarryPotter();
        redirectAttributes.addFlashAttribute("message", "Harry Potter was added if it was missing.");
        return "redirect:/books/HarryPotter";
    }

    private List<String> splitThemes(String themeText) {
        if (themeText == null || themeText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(themeText.split(","))
                .map(String::trim)
                .filter(theme -> !theme.isBlank())
                .toList();
    }
}
