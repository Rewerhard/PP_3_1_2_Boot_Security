package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    @Override
    @Transactional
    public void add(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.delete(getUserById(id));
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findUserByName(name);
    }

    @Override
    public void passwordChanged(User user, String encode) {
        encode = encoder.encode(encode);
        user.setPassword(encode);
    }
}
