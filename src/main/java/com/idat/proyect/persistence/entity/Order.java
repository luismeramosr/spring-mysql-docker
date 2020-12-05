package com.idat.proyect.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
@Table(name = "order")
public class Order {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Integer idOrder;

     private Integer idClient;

     private Double total;

     @Column(length = 100, name = "shipping_address")
     private String shippingAddress;

     @Column(name = "date_created")
     private LocalDateTime dateCreated;

     private Integer status;

}
