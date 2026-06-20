package com.sam.controller;

import com.sam.dto.AddressDTO;
import com.sam.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("users/{userId}/address")
    public ResponseEntity<AddressDTO> post(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long userId)
    {
        return new ResponseEntity<>(addressService.post(addressDTO,userId),HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/address/bulk-insert")
    public ResponseEntity<List<AddressDTO>> postAll(@Valid @RequestBody List<AddressDTO> dtos,@PathVariable("userId")Long userId)
    {
        return new ResponseEntity<>(addressService.bulkAddress(dtos,userId), HttpStatus.CREATED);
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<AddressDTO> get(@PathVariable Long id)
    {
        return new ResponseEntity<>(addressService.get(id),HttpStatus.OK);
    }

    @GetMapping("/address")
    public ResponseEntity<Page<AddressDTO>> getAll(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "2") int pageSize,
           @RequestParam(defaultValue = "id") String sort)
    {
        return new ResponseEntity<>(addressService.getAll(pageNumber,pageSize,sort),HttpStatus.OK);
    }

    @PutMapping("/address/update/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id)
    {
        return new ResponseEntity<>(addressService.get(id),HttpStatus.OK);
    }
}
