package com.example.demowithtests.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "addresses")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "address_has_active")
    private Boolean addressHasActive = Boolean.TRUE;
    @Column(name = "country")
    private String country;
    @Column(name = "city")
    private String city;
    @Column(name = "street")
    private String street;
}
