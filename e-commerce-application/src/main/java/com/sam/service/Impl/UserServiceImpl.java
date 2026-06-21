package com.sam.service.Impl;

import com.sam.dao.UserRepository;
import com.sam.dto.UserDTO;
import com.sam.dto.UsersDTO;
import com.sam.entity.User;
import com.sam.exception.UserNotFoundException;
import com.sam.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public UsersDTO postUser(UserDTO userDTO) {

        //DTO->ENTITY
        User user = modelMapper.map(userDTO,User.class);
        user.setActive(Boolean.TRUE);
        //Saved User
        User savedUser = userRepository.save(user);
        //Entity->DTO
        log.info("User created with Id {}",savedUser.getUserId());
        return modelMapper.map(savedUser,UsersDTO.class);
    }

    @Override
    public UsersDTO getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->  new UserNotFoundException("User with Id "+userId+" not found"));
        log.debug("User Fetched With Id {}",userId);
        return modelMapper.map(user,UsersDTO.class);
    }

    @Override
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        users.forEach(user->{
            UserDTO dto = modelMapper.map(user,UserDTO.class);
            userDTOS.add(dto);
        });

        return userDTOS;
    }

    @Override
    public List<UsersDTO> findAllPaginated(int pageNumber,int pageSize,String sort) {
        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC,sort)
        );
        Page<User> users = userRepository.findAll(pageRequest);
        return users.stream()
                .map(user -> modelMapper.map(user,UsersDTO.class)).toList();
    }

    @Transactional
    @Override
    public UsersDTO updateUser(Long userId,UsersDTO usersDTO) {
        log.info("Starting to update user {}",userId);
        User user = userRepository.findById(userId)
                .orElseThrow(()->  new UserNotFoundException("User with Id "+userId+" not found"));
        user.setName(usersDTO.getName());
        user.setEmail(usersDTO.getEmail());
        User updatedUser = userRepository.save(user);
        log.info("Updated User With Id {}",updatedUser.getUserId());
        return modelMapper.map(updatedUser,UsersDTO.class);
    }

    @Override
    public List<UsersDTO> findByNameOrEmail(String name, String email) {
        List<User> users = userRepository.findByNameOrEmail(name,email);
        return users.stream()
                .map(user -> modelMapper.map(user,UsersDTO.class)).toList();
    }

    @Transactional
    @Override
    public Long deleteUser(Long userId) {
        log.info("Starting to deleteUser {}",userId);
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User Not Found With the Id "+userId));
        user.setActive(Boolean.FALSE);
        String name = user.getName()+"(deleted)";
        user.setName(name);
        com.sam.entity.User saved=userRepository.save(user);
        log.info("Soft deleted user with {}",userId);
        return saved.getUserId();
    }
}
