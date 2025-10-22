package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * REST controller for managing authors
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors() {
        List<Author> authors = authorService.findAll();
        if(authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Object> getAuthor(@PathVariable int id) throws AuthorNotFoundException {
        Author author = authorService.findById(id);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @GetMapping("/authors/{id}/books")
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable int id) throws AuthorNotFoundException {
        Set<Book> books = authorService.findBooksByAuthorId(id);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping("/authors")
    public ResponseEntity<Object> createAuthor(@RequestBody Author author) {
        Author a = authorService.saveAuthor(author);
        return new ResponseEntity<>(a, HttpStatus.CREATED);
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<Object> updateAuthor(@RequestBody Author author, @PathVariable int id) throws AuthorNotFoundException {
        Author a = authorService.updateAuthor(author, id);
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

}
