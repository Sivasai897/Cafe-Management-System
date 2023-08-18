package com.in.cafe.serviceimpl;

import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.CategoryDao;
import com.in.cafe.dao.ProductDao;
import com.in.cafe.jwt.JwtFilter;
import com.in.cafe.pojo.Category;
import com.in.cafe.pojo.Product;
import com.in.cafe.service.ProductService;
import com.in.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductDao productDao;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CategoryDao categoryDao;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            Boolean isAdmin = jwtFilter.isAdmin();
            if (Boolean.TRUE.equals(isAdmin)) {
                if (validateProductMap(requestMap, false)) {
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product added Successfully", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Wrong_Message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isUpdate) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if (isUpdate) {
            product.setId(Integer.parseInt(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));

        return product;
    }

    private boolean validateProductMap(Map<String, String> requestMap, Boolean validateId) {
        if (requestMap.containsKey("name")) {
            if ((requestMap.containsKey("id") && validateId) || !validateId) {
                return true;
            }
        }
        return false;
    }
}
