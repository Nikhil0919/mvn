package com.library.bookservice.service;

import com.library.bookservice.dto.BookDTO;
import com.library.bookservice.entity.Book;
import com.library.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<BookDTO> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookDTO create(BookDTO dto) {
        Book b = new Book(dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getAvailableCopies());
        Book s = repo.save(b);
        return toDto(s);
    }

    @Transactional
    public boolean reserve(Long bookId, int quantity) {
        Book book = repo.findById(bookId).orElse(null);
        if (book == null) return false;
        if (book.getAvailableCopies() < quantity) return false;
        book.setAvailableCopies(book.getAvailableCopies() - quantity);
        repo.save(book);
        return true;
    }

    @Transactional
    public boolean release(Long bookId, int quantity) {
        Book book = repo.findById(bookId).orElse(null);
        if (book == null) return false;
        book.setAvailableCopies(book.getAvailableCopies() + quantity);
        repo.save(book);
        return true;
    }

    private BookDTO toDto(Book b) {
        BookDTO dto = new BookDTO();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());
        dto.setIsbn(b.getIsbn());
        dto.setAvailableCopies(b.getAvailableCopies());
        return dto;
    }
}

