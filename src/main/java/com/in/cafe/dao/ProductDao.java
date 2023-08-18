package com.in.cafe.dao;

import com.in.cafe.pojo.Product;
import com.in.cafe.wrapper.ProductWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<ProductWrapper> getAllProduct();
}
