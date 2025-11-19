package com.library.borrowservice.controller;

import com.library.borrowservice.dto.BorrowRequestDTO;
import com.library.borrowservice.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService service;
    public BorrowController(BorrowService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> borrow(@RequestBody BorrowRequestDTO req) { return service.borrow(req); }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> ret(@PathVariable Long id) { return service.returnBorrow(id); }

    @GetMapping
    public ResponseEntity<?> getAll() { return ResponseEntity.ok(service.getAll()); }
}
