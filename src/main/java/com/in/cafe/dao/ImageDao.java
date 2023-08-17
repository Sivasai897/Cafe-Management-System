package com.in.cafe.dao;

import com.in.cafe.pojo.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface ImageDao extends JpaRepository<ImageData, Integer> {
    Optional<ImageData> findByid(Integer id);
}
