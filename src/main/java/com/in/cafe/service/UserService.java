package com.in.cafe.service;

import com.in.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String,String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUser();
}
