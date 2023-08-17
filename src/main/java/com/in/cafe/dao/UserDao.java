package com.in.cafe.dao;

import com.in.cafe.pojo.User;
import com.in.cafe.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    List<User> FindByEmailId(@Param("email") String email, @Param("contactNumber") String contactNumber);

    User FIndByUserName(@Param("email") String email);

    List<UserWrapper> getAllUser();

    @Transactional
    @Modifying
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    List<String> getAllAdmin();

    User findByEmail(String email);
}
