package com.sam.dao;

import com.sam.constant.AddressType;
import com.sam.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {

    Page<Address> findAll(Pageable pageable);

    Optional<Address> findByAddressType(AddressType addressType);
}
