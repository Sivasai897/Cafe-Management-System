package com.in.cafe.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

@NamedQueries(
        {
                @NamedQuery(name = "Product.getAllProduct", query = "select new com.in.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.price,p.status,p.category.id,p.category.name) from Product p"),
                @NamedQuery(name = "Product.updateStatus", query = "update Product p set p.status=:status where p.id=:id"),
                @NamedQuery(name = "Product.getByCategory", query = "select new com.in.cafe.wrapper.ProductWrapper(p.id,p.name,p.price,p.description) from Product p where p.category.id=:id"),
                @NamedQuery(name = "Product.findProduct", query = "select new com.in.cafe.wrapper.ProductWrapper(p.id,p.name,p.price,p.description) from Product p where p.id=:id")
        }
)
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
public class Product implements Serializable {

    @Serial
    private static final Long serialVersionUID = 123455L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

}
