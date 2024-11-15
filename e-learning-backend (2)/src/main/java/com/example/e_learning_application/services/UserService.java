package com.example.e_learning_application.services;

import com.example.e_learning_application.entities.User;
import com.example.e_learning_application.repositories.UserRepository;
import com.example.e_learning_application.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void sendVerificationEmail(User user) {
        String token = jwtUtil.generateToken(user.getEmail());
        String link = "http://localhost:8080/api/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your email");
        message.setText("Click the link to verify your email: " + link);

        mailSender.send(message);
    }

    public boolean verifyEmail(String token) {
        String email = jwtUtil.extractUsername(token);
        if (email != null && !email.isEmpty()) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setVerified(true);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public void sendPasswordResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String resetToken = jwtUtil.generateToken(user.getEmail());
            String link = "http://localhost:8080/api/auth/reset-password?token=" + resetToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Reset your password");
            message.setText("Click the link to reset your password: " + link);

            mailSender.send(message);
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        String email = jwtUtil.extractUsername(token);
        if (email != null && !email.isEmpty()) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getEmail());
            }
        }
        return null;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}