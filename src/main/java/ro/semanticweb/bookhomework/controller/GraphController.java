package ro.semanticweb.bookhomework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ro.semanticweb.bookhomework.model.TripleView;
import ro.semanticweb.bookhomework.service.RdfBookService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/graph")
public class GraphController {

    private final RdfBookService rdfBookService;

    public GraphController(RdfBookService rdfBookService) {
        this.rdfBookService = rdfBookService;
    }

    @GetMapping
    public String graph(Model model) {
        model.addAttribute("pageType", "graph");
        return "graph";
    }

    @GetMapping("/current")
    @ResponseBody
    public List<TripleView> currentGraph() {
        return rdfBookService.triplesFromCurrentFile();
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<List<TripleView>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(rdfBookService.triplesFromUpload(file));
    }
}
