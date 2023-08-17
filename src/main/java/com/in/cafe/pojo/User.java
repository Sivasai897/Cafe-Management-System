package com.in.cafe.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

@NamedQueries(
        {
                @NamedQuery(name = "User.FindByEmailId", query = "select u from User u where u.email=:email OR u.contactNumber=:contactNumber"),
                @NamedQuery(name = "User.FIndByUserName", query = "select u from User u where u.email=:email"),
                @NamedQuery(name = "User.getAllUser", query = "select new com.in.cafe.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'"),
                @NamedQuery(name = "User.getAllAdmin", query = "select u.email from User u where u.role='admin'"),
                @NamedQuery(name = "User.updateStatus", query = "Update User u set u.status=:status where u.id=:id")
        }
)

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;

}
