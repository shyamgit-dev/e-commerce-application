package com.sam.entity;

import com.sam.constant.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private String zipCode;
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
