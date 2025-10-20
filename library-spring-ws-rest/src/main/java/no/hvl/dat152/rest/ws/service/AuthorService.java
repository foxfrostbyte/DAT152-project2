/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private BookRepository bookRepository;
	
	public Author findById(int id) throws AuthorNotFoundException {
		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
		return author;
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

	public List<Book> findBooksByAuthorId(int id) {
		return bookRepository.findBooksByAuthorId(id);
	}
}
