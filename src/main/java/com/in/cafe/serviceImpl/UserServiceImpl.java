package com.in.cafe.serviceImpl;

import com.in.cafe.JWT.CustomerUserDetailService;
import com.in.cafe.JWT.JwtFilter;
import com.in.cafe.JWT.JwtUtil;
import com.in.cafe.POJO.User;
import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.UserDao;
import com.in.cafe.service.UserService;
import com.in.cafe.utils.CafeUtils;
import com.in.cafe.utils.EmailUtils;
import com.in.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    EmailUtils emailUtils;

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
                    return new ResponseEntity<>("{\"token\":\"" +
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

    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                List<UserWrapper> userList = userDao.getAllUser();
                return new ResponseEntity<>(userList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Integer id = Integer.parseInt(requestMap.get("id"));
                String status = requestMap.get("status");
                Optional<User> optionalUser = userDao.findById(id);
                if (optionalUser.isPresent()) {
                    userDao.updateStatus(id, status);
                    sendEmailToAllAdmin(status, optionalUser.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("SUCESSFULLY UPDATED USER STATUS", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("USER DOES NOT EXIST", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Wrong_Message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            String oldPassword = requestMap.get("oldPassword");
            String newPassword1 = requestMap.get("newPassword1");
            String newPassword2 = requestMap.get("newPassword2");
            String email = jwtFilter.getCurrentUser();
            User user = userDao.findByEmail(email);
            String currentPassword = user.getPassword();
            if (oldPassword.equals(currentPassword)) {
                if (!(oldPassword.equals(newPassword1)) && (newPassword2.equals(newPassword2))) {
                    user.setPassword(newPassword1);
                    userDao.save(user);
                    return CafeUtils.getResponseEntity("Successfully Changed Password", HttpStatus.OK);
                } else if (oldPassword.equals(newPassword1) && newPassword1.equals(newPassword2)) {
                    return CafeUtils.getResponseEntity("YOU ENTERED OLD PASSWORD AS NEW PASSWORD PLEASE ENTER A NEW PASSWORD", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("YOU ENTERED INCORRECT PASSWORD SECOND TIME", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity("YOU ENTERED WRONG OLD PASSWORD", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user)) {
                emailUtils.forgotPassword(user.getEmail(),"Credentials By Cafe Management System",user.getPassword());
            }
            return CafeUtils.getResponseEntity("check your mail for credentials",HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Wrong_Message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendEmailToAllAdmin(String status, String user, List<String> allAdmin) {
        String currentAdminEmail = jwtFilter.getCurrentUser();
        allAdmin.remove(currentAdminEmail);
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(currentAdminEmail, "ACCOUNT APPROVED",
                    "User:-" + user + "\nis Approved By \nAdmin:-" + currentAdminEmail, allAdmin);
        } else {
            emailUtils.sendSimpleMessage(currentAdminEmail, "ACCOUNT Diabled",
                    "User:-" + user + "\nis Disabled By \nAdmin:-" + currentAdminEmail, allAdmin);
        }


    }
}