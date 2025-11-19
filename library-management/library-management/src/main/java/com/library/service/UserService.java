package com.library.service;

import com.library.dto.UserDTO;
import com.library.entity.User;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO dto) {
        User user = new User(dto.getName(), dto.getEmail(), dto.getMembershipType());
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMembershipType(user.getMembershipType());
        return dto;
    }
}
