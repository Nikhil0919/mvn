package com.library.service;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookDTO createBook(BookDTO dto) {
        Book book = new Book(dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getAvailableCopies());
        Book saved = bookRepository.save(book);
        return toDto(saved);
    }

    public boolean decreaseAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.getAvailableCopies() <= 0) {
            return false;
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return true;
    }

    public void increaseAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    private BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setAvailableCopies(book.getAvailableCopies());
        return dto;
    }
}
