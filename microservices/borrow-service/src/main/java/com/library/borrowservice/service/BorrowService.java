package com.library.borrowservice.service;

import com.library.borrowservice.dto.BorrowRequestDTO;
import com.library.borrowservice.entity.BorrowRecord;
import com.library.borrowservice.repository.BorrowRecordRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class BorrowService {

    private final BorrowRecordRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String userServiceBase = "http://localhost:8082/api/users";
    private final String bookServiceBase = "http://localhost:8081/api/books";

    public BorrowService(BorrowRecordRepository repo) { this.repo = repo; }

    @Transactional
    public ResponseEntity<?> borrow(BorrowRequestDTO req) {
        // 1. validate user
        try {
            ResponseEntity<String> ur = restTemplate.postForEntity(userServiceBase + "/validate", new UserValidate(req.getUserId()), String.class);
            if (ur.getStatusCode().is4xxClientError() || ur.getStatusCode().is5xxServerError()) {
                return ResponseEntity.status(400).body(new ErrorResp("invalid_user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(502).body(new ErrorResp("user_service_down"));
        }

        // 2. reserve book
        try {
            ResponseEntity<String> br = restTemplate.postForEntity(bookServiceBase + "/" + req.getBookId() + "/reserve", new ReserveReq(1), String.class);
            if (br.getStatusCode() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(409).body(new ErrorResp("book_unavailable"));
            }
            if (!br.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(502).body(new ErrorResp("book_service_error"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(502).body(new ErrorResp("book_service_down"));
        }

        // 3. persist borrow record
        try {
            LocalDate now = LocalDate.now();
            BorrowRecord r = new BorrowRecord(req.getUserId(), req.getBookId(), now, now.plusDays(req.getDays()), "BORROWED");
            BorrowRecord saved = repo.save(r);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            // compensation: release book
            try {
                restTemplate.postForEntity(bookServiceBase + "/" + req.getBookId() + "/release", new ReserveReq(1), String.class);
            } catch (Exception ignored) {}
            return ResponseEntity.status(500).body(new ErrorResp("failed_to_persist"));
        }
    }

    @Transactional
    public ResponseEntity<?> returnBorrow(Long id) {
        BorrowRecord r = repo.findById(id).orElse(null);
        if (r == null) return ResponseEntity.status(404).body(new ErrorResp("not_found"));
        if (r.getReturnDate() != null) return ResponseEntity.status(409).body(new ErrorResp("already_returned"));
        r.setReturnDate(LocalDate.now());
        r.setStatus("RETURNED");
        repo.save(r);

        // call book service to release
        try {
            restTemplate.postForEntity(bookServiceBase + "/" + r.getBookId() + "/release", new ReserveReq(1), String.class);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(r);
    }

    // Return all borrow records
    public java.util.List<BorrowRecord> getAll() {
        return repo.findAll();
    }

    static class UserValidate { public Long userId; public UserValidate(Long id){this.userId=id;} }
    static class ReserveReq { public int quantity; public ReserveReq(int q){this.quantity=q;} }
    static class ErrorResp { public String error; public ErrorResp(String e){this.error=e;} }
}
