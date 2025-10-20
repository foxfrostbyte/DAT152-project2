/**
 * 
 */
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
 * 
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
	
	// TODO - getBooksByAuthorId (@Mappings, URI, and method)
	
	// TODO - createAuthor (@Mappings, URI, and method)
	
	// TODO - updateAuthor (@Mappings, URI, and method)


}
