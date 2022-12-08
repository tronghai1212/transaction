package org.growhack.bank.portal.entity;

import lombok.Getter;
import lombok.Setter;

//import javax.persistence.*;
//@Entity
//@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String email;
    private String phone;
    private String storeName;
    private String state;
    private String role;
    private Long partnerId;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(name = "username")
//    private String username;
//
//    @Column(name = "password")
//    private String password;
//
//    @Column(name = "first_name")
//    private String firstName;
//
//    @Column(name = "email")
//    private String email;
//
//    @Column(name = "phone")
//    private String phone;
//
//    @Column(name = "store_name")
//    private String storeName;
//
//    @Column(name = "state")
//    @Enumerated(EnumType.STRING)
//    private String state;
//
//    @Column(name = "role")
//    @Enumerated(EnumType.STRING)
//    private String role;
//
//    @Column(name = "partner_id")
//    private Long partnerId;


}
