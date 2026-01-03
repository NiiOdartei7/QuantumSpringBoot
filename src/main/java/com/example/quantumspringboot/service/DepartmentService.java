package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.DepartmentDTO;
import com.example.quantumspringboot.dto.PolicyRequest;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<DepartmentDTO> findAllDepartment() throws EntityDoesNotExistException {

        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDTO> departmentDTOS = new ArrayList<>();
        for(Department department: departments){
            DepartmentDTO departmentDTO = objectMapper.convertValue(department, DepartmentDTO.class);
            departmentDTOS.add(departmentDTO);
        }
        return departmentDTOS;
    }

    public String createEncryptionPolicy(PolicyRequest policyRequest){
        if(policyRequest.getDepartments().size() == 1){
            return "Department::"+policyRequest.getDepartments().get(0) +" && " +
                    "Clearance::"+policyRequest.getClearance();
        } else if (policyRequest.getDepartments().size() ==2 ) {
            return "(Department::"+policyRequest.getDepartments().get(0)
                    + " || " + "Department::" +policyRequest.getDepartments().get(1)
                    +")"
                    +" && " +
                    "Clearance::"+policyRequest.getClearance();
        }
        throw new IllegalArgumentException("Wrong Arguments");
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
