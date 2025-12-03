package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.DepartmentDTO;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ObjectMapper objectMapper;



    public DepartmentDTO findDepartment(String name) throws EntityDoesNotExistException {


        if(departmentRepository.existsDepartmentByNameIgnoreCase(name)){
            Department department =  departmentRepository.findDepartmentByName(name);
            return objectMapper.convertValue(department, DepartmentDTO.class);
        }
        throw new EntityDoesNotExistException("Department Does Not Exist");

    }

    public DepartmentDTO createDepartment(String name) throws EntityDoesNotExistException {


        if(departmentRepository.existsDepartmentByNameIgnoreCase(name)){
            throw new EntityDoesNotExistException("Department Already Exists");
        }
        Department department = new Department(name);
        departmentRepository.save(department);
        return objectMapper.convertValue(department, DepartmentDTO.class);
    }

}
