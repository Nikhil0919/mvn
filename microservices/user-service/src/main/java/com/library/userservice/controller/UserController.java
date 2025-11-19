package com.library.userservice.controller;

import com.library.userservice.dto.UserDTO;
import com.library.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) { return ResponseEntity.status(201).body(service.create(dto)); }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        UserDTO dto = service.get(id);
        if (dto == null) return ResponseEntity.status(404).body(new SimpleError("User not found"));
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestBody ValidateRequest req) {
        UserDTO dto = service.get(req.userId);
        if (dto == null) return ResponseEntity.status(404).body(new ValidateResponse(false, "not_found"));
        if (!dto.isActive()) return ResponseEntity.ok(new ValidateResponse(false, "inactive"));
        return ResponseEntity.ok(new ValidateResponse(true, null));
    }

    static class ValidateRequest { public Long userId; }
    static class ValidateResponse { public boolean valid; public String reason; public ValidateResponse(boolean v, String r){this.valid=v;this.reason=r;} }
    static class SimpleError { public String error; public SimpleError(String e){this.error=e;} }
}

