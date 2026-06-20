package com.sam.service.Impl;

import com.sam.dao.AddressRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.AddressDTO;
import com.sam.entity.Address;
import com.sam.entity.User;
import com.sam.exception.AddressNotFoundException;
import com.sam.exception.UserNotFoundException;
import com.sam.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service("addressService")
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public AddressDTO post(AddressDTO addressDTO,Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not exists"));
        Address address = modelMapper.map(addressDTO,Address.class);
        address.setUser(user);
        com.sam.entity.Address savedAddress =addressRepository.save(address);
        return modelMapper.map(address,AddressDTO.class);
    }

    @Transactional
    @Override
    public List<AddressDTO> bulkAddress(List<AddressDTO> dtos,Long userId) {
        List<Address> addresses = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User not exists"));
        for (AddressDTO dto : dtos) {
            Address address = modelMapper.map(dto,Address.class);
            address.setUser(user);
            addresses.add(address);
            addressRepository.saveAll(addresses);
        }
        return addresses.stream()
                .map(address1 -> modelMapper.map(address1,AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO get(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(()->new AddressNotFoundException("No Address Found with Id "+id));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public Page<AddressDTO> getAll(int pageNumber, int pageSize, String sort) {
        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.ASC,sort)

        );
        Page<Address>  addresses = addressRepository.findAll(pageRequest);
        return addresses.map(address -> modelMapper.map(address,AddressDTO.class));
    }

    @Override
    public AddressDTO update(Long id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(()->new AddressNotFoundException("No Address Found with Id "+id));
        address.setAddressType(addressDTO.getAddressType());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setZipCode(addressDTO.getZipCode());
        address.setStreet(addressDTO.getStreet());
        address.setDefault(addressDTO.isDefault());
        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress,AddressDTO.class);
    }
}
