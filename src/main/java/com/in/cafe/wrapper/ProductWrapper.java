package com.in.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapper {

    Integer id;
    String name;
    String description;
    Integer price;
    String status;
    Integer categoryId;
    String categoryName;

    public ProductWrapper(Integer id,String name,Integer price,String description) {
        this.id=id;
        this.name=name;
        this.price=price;
        this.description=description;
    }

}
