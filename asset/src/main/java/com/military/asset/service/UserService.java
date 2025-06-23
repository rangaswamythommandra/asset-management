package com.military.asset.service;

import com.military.asset.model.User;
import com.military.asset.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public List<User> findAll() { return userRepository.findAll(); }
    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }
    public User save(User user) { return userRepository.save(user); }
    public void deleteById(Long id) { userRepository.deleteById(id); }
}