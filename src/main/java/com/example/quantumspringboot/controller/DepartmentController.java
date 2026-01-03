package com.example.quantumspringboot.controller;

import com.example.quantumspringboot.dto.*;
import com.example.quantumspringboot.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepartmentController {
    private final DepartmentService service;


    @PostMapping("/v1/department")
    public ResponseEntity<DepartmentDTO> addDepartment(@Valid
                                                           @RequestBody DepartmentRequestDTO requestDTO
    ){
        return ResponseEntity.ok(service.createDepartment(requestDTO.getName()));
    }

    @GetMapping("/v1/department")
    public ResponseEntity<List<DepartmentDTO>> getDepartments(
    ){
        return ResponseEntity.ok(service.findAllDepartment());
    }

    @PostMapping("/v1/department/policy")
    public ResponseEntity<String> createPolicy(@Valid
                                                       @RequestBody PolicyRequest policyRequest
    ){
        return ResponseEntity.ok(service.createEncryptionPolicy(policyRequest));
    }


}
