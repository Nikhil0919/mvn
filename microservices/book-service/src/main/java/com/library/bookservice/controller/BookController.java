package com.library.bookservice.controller;

import com.library.bookservice.dto.BookDTO;
import com.library.bookservice.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO dto) { return ResponseEntity.status(201).body(service.create(dto)); }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id, @RequestBody(required = false) ReserveRequest req) {
        int qty = (req == null) ? 1 : req.quantity;
        boolean ok = service.reserve(id, qty);
        if (ok) return ResponseEntity.ok().body(new SimpleResponse(true, null));
        return ResponseEntity.status(409).body(new SimpleResponse(false, "insufficient_copies_or_not_found"));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<?> release(@PathVariable Long id, @RequestBody(required = false) ReserveRequest req) {
        int qty = (req == null) ? 1 : req.quantity;
        boolean ok = service.release(id, qty);
        if (ok) return ResponseEntity.ok().body(new SimpleResponse(true, null));
        return ResponseEntity.status(404).body(new SimpleResponse(false, "not_found"));
    }

    static class ReserveRequest { public int quantity = 1; }
    static class SimpleResponse { public boolean success; public String reason; public SimpleResponse(boolean s, String r){this.success=s;this.reason=r;} }
}

