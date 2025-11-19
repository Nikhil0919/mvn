package com.library.userservice.service;

import com.library.userservice.dto.UserDTO;
import com.library.userservice.entity.User;
import com.library.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserDTO create(UserDTO dto) {
        User u = new User(dto.getName(), dto.getEmail(), dto.isActive());
        User s = repo.save(u);
        return toDto(s);
    }

    public UserDTO get(Long id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    public List<UserDTO> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private UserDTO toDto(User u) {
        UserDTO d = new UserDTO();
        d.setId(u.getId()); d.setName(u.getName()); d.setEmail(u.getEmail()); d.setActive(u.isActive());
        return d;
    }
}

