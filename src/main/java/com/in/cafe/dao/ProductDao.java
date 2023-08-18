package com.in.cafe.dao;

import com.in.cafe.pojo.Product;
import com.in.cafe.wrapper.ProductWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<ProductWrapper> getAllProduct();
    @Modifying
    @Transactional
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<ProductWrapper> getByCategory(@Param("id") Integer id);

    ProductWrapper findProduct(@Param("id") Integer id);
}
