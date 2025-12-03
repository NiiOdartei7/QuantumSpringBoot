package com.example.quantumspringboot.repository;

import com.example.quantumspringboot.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer>{

    Department findDepartmentByName(String name);

    boolean existsDepartmentByNameIgnoreCase(String name);
}
