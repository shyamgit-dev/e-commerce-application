package com.sam.dao;

import com.sam.constant.AddressType;
import com.sam.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {

    Page<Address> findAll(Pageable pageable);

    Optional<Address> findByAddressType(AddressType addressType);

    @Query("SELECT a from Address a WHERE a.id=:addressId and a.user.userId=:userId")
    Optional<Address> findByIdAndUserId(@Param("addressId") long addressId, @Param("userId") Long userId);
}
