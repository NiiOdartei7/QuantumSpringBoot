package com.example.quantumspringboot.repository;

import com.example.quantumspringboot.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer>{

    Department findDepartmentByName(String name);

    boolean existsDepartmentByNameIgnoreCase(String name);

    Optional<Department> findByName(String name);
}
