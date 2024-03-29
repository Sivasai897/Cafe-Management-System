package com.in.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;
    private String name;
    private String contactNumber;
    private String email;
    private String status;
    private String  role;
}
