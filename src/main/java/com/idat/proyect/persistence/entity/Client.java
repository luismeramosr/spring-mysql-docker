package com.idat.proyect.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@Table(name = "client")
public class Client {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Integer idClient;

     @Column(length = 30)
     private String username;

     @Column(length = 80)
     private String password;

     @Column(name = "idRole")
     private Integer idRole;

     @Column(name = "first_name", length = 45)
     private String firstName;

     @Column(name = "last_name", length = 45)
     private String lastName;

     @Column(length = 100)
     private String address;

     @Column(length = 9)
     private String phone;

     @Column(length = 45)
     private String email;

     private Integer active;

     // un cliente pude muchos roles
     @OneToMany
     @JoinColumn(name = "id")
     private List<Role> roles;

     
}
