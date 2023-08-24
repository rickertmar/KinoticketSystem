package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    public boolean isEmailUnique(String email){
        return userRepository.countByEmail(email) == 0;
    }
    public User create(User user){
        if(!isEmailUnique(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_NOT_UNIQUE");
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }
    public User update(User user, long id){
        return userRepository.save(user);
    }
    public void delete(long id){
        userRepository.deleteById(id);
    }
    public User findById(long id){
        return userRepository.findById(id).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    //AUTH
}
