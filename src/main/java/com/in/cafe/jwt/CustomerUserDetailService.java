package com.in.cafe.jwt;

import com.in.cafe.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    private com.in.cafe.pojo.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Step5a:\nInto the LoaduserByUserName Method " + username);
        userDetail = userDao.FIndByUserName(username);
        if (!Objects.isNull(userDetail)) {
            log.info("Step5b:\nUser Detail Object is not null {}" + userDetail);
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User Not Found");
        }

    }

    public com.in.cafe.pojo.User getUserDetail() {
        log.info("Step5c:\nInto the UserDetails Method {}" + userDetail);
        return userDetail;
    }
}
