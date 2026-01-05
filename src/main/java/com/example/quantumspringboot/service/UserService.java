package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.DepartmentDTO;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.dto.UserResponseDTO;
import com.example.quantumspringboot.entity.Clearance;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.Role;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.example.quantumspringboot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;


    public UserResponseDTO createUser(UserRequestDTO user) throws EntityAlreadyExistsException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User convertedUser = objectMapper.convertValue(user, User.class);
        if(userRepository.existsUserByEmail(convertedUser.getEmail())){
            throw new EntityAlreadyExistsException("User Already Exists");
        }
        User user1 = userRepository.save(convertedUser);
        return objectMapper.convertValue(user1, UserResponseDTO.class);

    }
    public UserResponseDTO findUserByEmail(String email) throws EntityDoesNotExistException {


        if(userRepository.existsUserByEmail(email)){
//            return userRepository.findUserByEmail(email);
            return objectMapper.convertValue(userRepository.findUserByEmail(email), UserResponseDTO.class);
        }
        throw new EntityDoesNotExistException("User Does Not Exist");

    }

    public UserResponseDTO findUserById(UUID ID) throws EntityDoesNotExistException {


        if(userRepository.existsUserById(ID)){
//            return userRepository.findUserByEmail(email);
            return objectMapper.convertValue(userRepository.findUserById(ID), UserResponseDTO.class);
        }
        throw new EntityDoesNotExistException("User Does Not Exist");

    }

    public UserResponseDTO updateUser(String email, String deptName, String level, String sentRole){
        Clearance clearance;
        Role role;
        if(level.equals((Clearance.HIGH).toString())){
            clearance = Clearance.HIGH;

        }
        else{
            clearance = Clearance.LOW;
        }
        if(sentRole.equals((Role.ADMIN).toString())){
            role = Role.ADMIN;

        }
        else{
            role = Role.USER;
        }

        if(!departmentRepository.existsDepartmentByNameIgnoreCase(deptName)){
            throw new EntityDoesNotExistException("Department does not exist");
        }
        Department department1 = departmentRepository.findDepartmentByName(deptName);
        if(userRepository.existsUserByEmail(email)){
            User foundUser = userRepository.findUserByEmail(email);
            foundUser.setDepartment(department1);
            foundUser.setClearance(clearance);
            foundUser.definePolicy();
            foundUser.setRole(role);
            UUID id =foundUser.getId();
            userRepository.save(foundUser);

            return new UserResponseDTO(id,email, foundUser.getUserAccessPolicy());

        }
        else throw new EntityDoesNotExistException("User does not exist");



    }




}
