package com.sam.controller;

import com.sam.dto.UserDTO;
import com.sam.dto.UsersDTO;
import com.sam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> post(@RequestBody UserDTO userDTO)
    {
        return new ResponseEntity<>(userService.postUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable Long id)
    {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll()
    {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<UsersDTO>> getAllPaginated(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "2") int pageSize,
           @RequestParam(defaultValue = "userId") String sort
    )
    {
        return new ResponseEntity<>(userService.findAllPaginated(pageNumber,pageSize,sort), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long userId, @RequestBody UsersDTO usersDTO)
    {
        return new ResponseEntity<>(userService.updateUser(userId,usersDTO),HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersDTO>> findByNameOrEmail(
           @RequestParam String name,
           @RequestParam String email)
    {
        return new ResponseEntity<>(userService.findByNameOrEmail(name,email),HttpStatus.OK);
    }

}
