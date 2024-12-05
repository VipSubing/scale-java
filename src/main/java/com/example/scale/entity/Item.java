package com.example.scale.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Item {
    private Long id;
    private String name;
    private Integer age;
    private String sex;
    private LocalDate birthday;
    private String address;
    private String phone;
    private String email;
    private String remark;
} 