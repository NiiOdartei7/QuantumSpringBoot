package com.example.quantumspringboot.seeders;

import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.dto.UserResponseDTO;
import com.example.quantumspringboot.entity.Clearance;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.Role;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.example.quantumspringboot.repository.UserRepository;
import com.example.quantumspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SeederCommandLineRunner implements CommandLineRunner {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Department department = new Department("Artificial Intelligence");
        Department department1 = new Department("Robotics");

        departmentRepository.save(department);
        departmentRepository.save(department1);
        String password = passwordEncoder.encode("password123");
        User user = new User("admin@email.com",password, Role.ADMIN);
        User user1 = new User();
        user1.setDepartment(department);
        user1.setEmail("user@user.com");

        user1.setPassword(password);
        user1.setRole(Role.USER);
        user1.setClearance(Clearance.HIGH);
        user1.setUserAccessPolicy("Department::Artificial Intelligence && Clearance::HIGH");
        userRepository.save(user);
        userRepository.save(user1);

    }
}
