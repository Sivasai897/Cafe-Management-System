package com.in.cafe.serviceImpl;

import com.in.cafe.JWT.CustomerUserDetailService;
import com.in.cafe.JWT.JwtUtil;
import com.in.cafe.POJO.User;
import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.UserDao;
import com.in.cafe.service.UserService;
import com.in.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomerUserDetailService customerUserDetailService;
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Into the SingUp Method {}", requestMap);
        try {
            if (requestMapValidation(requestMap)) {
                List<User> user = userDao.FindByEmailId(requestMap.get("email"), requestMap.get("contactNumber"));


                if (user.isEmpty()) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Registration is Successfull", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User Already Exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.Wrong_Message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Boolean requestMapValidation(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {

        // log.info("Step1:\ninto login Method");
        try {
            //    log.info("Step2:\ninto try block");
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            //  log.info(" Step3:\nsuccessfully retrieved aut object{}"+auth);
            if (auth.isAuthenticated()) {
                //log.info("Step4:\nuser is succesfully authenticated");
                if (customerUserDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    // log.info("Step6:\nuser status is active");
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailService.getUserDetail().getEmail(),
                                    customerUserDetailService.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    // log.info("user status is inactive");
                    return new ResponseEntity<>("{\"Message\":\"Wait for Admin Approval\"}", HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("something went wrong in login business logic");
        return new ResponseEntity<>("{\"Message\":\"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }
}