package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * Service layer for managing authors
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	
	public Author findById(int id) throws AuthorNotFoundException {
        return authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
	}

	public Author saveAuthor(Author author) {
		return authorRepository.save(author);
	}

	public Author updateAuthor(Author author, int id) throws AuthorNotFoundException {
		Author currentAuthor = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));

		currentAuthor.setFirstname(author.getFirstname());
		currentAuthor.setLastname(author.getLastname());
		currentAuthor.setBooks(author.getBooks());

		return authorRepository.save(currentAuthor);
	}

	public List<Author> findAll() {
		return (List<Author>) authorRepository.findAll();
	}

	// TODO public void deleteById(int id) throws AuthorNotFoundException
	// Unsure if implementation needed. Lacks to-do in controller + test for it. Asked teacher. Might delete later.

	public Set<Book> findBooksByAuthorId(int id) throws AuthorNotFoundException {
		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
		return author.getBooks();
	}
}
