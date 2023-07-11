package com.in.cafe.serviceImpl;

import com.in.cafe.POJO.User;
import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.UserDao;
import com.in.cafe.service.UserService;
import com.in.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Into the SingUp Method {}",requestMap);
        try{
            if(requestMapValidation(requestMap)){
                List<User> user=userDao.FindByEmailId(requestMap.get("email"),requestMap.get("contactNumber"));


                if(user.isEmpty()){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Registration is Successfull",HttpStatus.OK);
                }
                else{
                    return CafeUtils.getResponseEntity("User Already Exists",HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.Wrong_Message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Boolean requestMapValidation(Map<String,String> requestMap){
        return requestMap.containsKey("name")&&requestMap.containsKey("contactNumber")
                &&requestMap.containsKey("email")&&requestMap.containsKey("password");
    }
    private User getUserFromMap(Map<String,String> requestMap){
        User user=new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}
