package com.sam.service;

import com.sam.dto.UserDTO;
import com.sam.dto.UsersDTO;
import com.sam.entity.User;

import java.util.List;

public interface UserService {

    public UserDTO postUser(UserDTO user);
    public UserDTO getUser(Long userId);
    public List<UserDTO> getAll();
    public List<UsersDTO> findAllPaginated(int pageNumber,int pageSize,String sort);
    public UsersDTO updateUser(Long userId,UsersDTO usersDTO);
    public List<UsersDTO> findByNameOrEmail(String name,String email);
}
