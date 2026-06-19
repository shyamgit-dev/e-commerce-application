package com.sam.dao;

import com.sam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    Page<User> findAll(Pageable pageable);

    List<User> findByNameOrEmail(String name,String email);
}
