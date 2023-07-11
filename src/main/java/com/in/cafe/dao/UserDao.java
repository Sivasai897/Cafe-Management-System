package com.in.cafe.dao;

import com.in.cafe.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User,Integer> {

    List<User> FindByEmailId(@Param("email") String email, @Param("contactNumber") String contactNumber);

}
