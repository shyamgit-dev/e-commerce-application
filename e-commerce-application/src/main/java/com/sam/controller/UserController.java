package com.sam.controller;

import com.sam.dto.UserDTO;
import com.sam.dto.UserPostRegistration;
import com.sam.dto.UserRegistrationDTO;
import com.sam.dto.UsersDTO;
import com.sam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserPostRegistration> register(@RequestBody UserRegistrationDTO dto)
    {
        return new ResponseEntity<>(userService.registerUser(dto), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<UsersDTO> post(@Valid @RequestBody UserDTO userDTO)
    {
        return new ResponseEntity<>(userService.postUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseEntity<UsersDTO> get(@PathVariable Long id)
    {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAll()
    {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsersDTO>> getAllPaginated(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "2") int pageSize,
           @RequestParam(defaultValue = "userId") String sort
    )
    {
        return new ResponseEntity<>(userService.findAllPaginated(pageNumber,pageSize,sort), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody UsersDTO usersDTO)
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

    @PatchMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> softDeleteUser(@PathVariable Long userId)
    {
        String result = "User with Id "+userService.deleteUser(userId)+" has been deleted";
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
