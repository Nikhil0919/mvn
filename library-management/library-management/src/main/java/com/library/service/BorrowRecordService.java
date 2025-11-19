package com.library.service;

import com.library.dto.BorrowRequestDTO;
import com.library.entity.BorrowRecord;
import com.library.repository.BorrowRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;

    public BorrowRecordService(BorrowRecordRepository borrowRecordRepository,
                               BookService bookService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookService = bookService;
    }

    public String borrowBook(BorrowRequestDTO request) {
        boolean available = bookService.decreaseAvailableCopies(request.getBookId());
        if (!available) {
            return "Book not available";
        }

        BorrowRecord record = new BorrowRecord(LocalDate.now(), request.getBookId(), request.getUserId());
        borrowRecordRepository.save(record);
        return "Book borrowed successfully";
    }

    public String returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId).orElse(null);
        if (record == null) {
            return "Borrow record not found";
        }

        if (record.getReturnDate() != null) {
            return "Already returned";
        }

        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);

        bookService.increaseAvailableCopies(record.getBookId());
        return "Book returned successfully";
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }
}
