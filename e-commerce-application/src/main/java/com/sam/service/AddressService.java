package com.sam.service;

import com.sam.dto.AddressDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface AddressService {
    public AddressDTO post(AddressDTO addressDTO,Long userId);
    public List<AddressDTO> bulkAddress(List<AddressDTO> dtos,Long userId);
    public AddressDTO get(Long id);
    public Page<AddressDTO> getAll(int pageNumber, int pageSize, String sort);
    public AddressDTO update(Long id,AddressDTO addressDTO);
}
