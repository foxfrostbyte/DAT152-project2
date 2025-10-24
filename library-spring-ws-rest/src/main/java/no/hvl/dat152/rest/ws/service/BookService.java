package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * Service layer for managing books
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;


	public Book saveBook(Book book) {
		return bookRepository.save(book);
	}
	
	public List<Book> findAll(){
		return (List<Book>) bookRepository.findAll();
	}
	
	
	public Book findByISBN(String isbn) throws BookNotFoundException {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
	}

	public Book updateBook(Book book, String isbn) throws BookNotFoundException {
		Book b = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));

		b.setTitle(book.getTitle());
		b.setAuthors(book.getAuthors());

		return b;
	}

	public Set<Author> findAuthorsOfBookByISBN(String isbn) throws BookNotFoundException {
		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		return book.getAuthors();
	}

	public void deleteByISBN(String isbn) throws BookNotFoundException {
		Book b = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		bookRepository.delete(b);
	}
	
}
