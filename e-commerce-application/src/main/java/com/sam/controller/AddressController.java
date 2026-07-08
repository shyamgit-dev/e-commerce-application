package com.sam.controller;

import com.sam.dto.AddressDTO;
import com.sam.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    @PreAuthorize("hasRole('USER') or @userSecurity.isOwner(#userId)")
    @PostMapping("users/{userId}/address")
    public ResponseEntity<AddressDTO> post(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long userId)
    {
        return new ResponseEntity<>(addressService.post(addressDTO,userId),HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN') or @userSecurity.isOwner(#userId)")
    @PostMapping("/users/{userId}/address/bulk-insert")
    public ResponseEntity<List<AddressDTO>> postAll(@Valid @RequestBody List<AddressDTO> dtos,@PathVariable("userId")Long userId)
    {
        return new ResponseEntity<>(addressService.bulkAddress(dtos,userId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER') or @userSecurity.isOwner(#userId)")
    @GetMapping("/address/{id}")
    public ResponseEntity<AddressDTO> get(@PathVariable Long id)
    {
        return new ResponseEntity<>(addressService.get(id),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/address")
    public ResponseEntity<Page<AddressDTO>> getAll(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "2") int pageSize,
           @RequestParam(defaultValue = "id") String sort)
    {
        return new ResponseEntity<>(addressService.getAll(pageNumber,pageSize,sort),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/address/update/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id)
    {
        return new ResponseEntity<>(addressService.get(id),HttpStatus.OK);
    }
}
