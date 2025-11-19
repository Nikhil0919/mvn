package com.library.controller;

import com.library.dto.BorrowRequestDTO;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @PostMapping
    public ResponseEntity<String> borrowBook(@RequestBody BorrowRequestDTO request) {
        return ResponseEntity.ok(borrowRecordService.borrowBook(request));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        return ResponseEntity.ok(borrowRecordService.getAllBorrowRecords());
    }
}
