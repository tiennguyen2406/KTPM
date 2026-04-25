package com.movie.ticket.user.service;

import com.movie.ticket.user.dto.UserRegisteredEvent;
import com.movie.ticket.user.entity.User;
import com.movie.ticket.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public User register(User user) {
        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getUsername());
        
        // Publish event
        UserRegisteredEvent e = new UserRegisteredEvent(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        kafkaTemplate.send("USER_REGISTERED", e);
        
        return savedUser;
    }

    public User login(String username, String password) {
        return userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));    }
}
