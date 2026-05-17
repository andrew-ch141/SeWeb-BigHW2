package ro.semanticweb.bookhomework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.semanticweb.bookhomework.service.RdfBookService;

@Controller
public class HomeController {

    private final RdfBookService rdfBookService;

    public HomeController(RdfBookService rdfBookService) {
        this.rdfBookService = rdfBookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("bookCount", rdfBookService.findAllBooks().size());
        model.addAttribute("users", rdfBookService.findAllUsers());
        model.addAttribute("pageType", "home");
        return "home";
    }
}
