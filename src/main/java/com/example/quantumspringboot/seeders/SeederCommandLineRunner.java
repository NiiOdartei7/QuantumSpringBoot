package com.example.quantumspringboot.seeders;


import com.example.quantumspringboot.entity.Clearance;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.Role;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.example.quantumspringboot.repository.UserRepository;
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
        Department aiDepartment = departmentRepository
                .findByName("Artificial Intelligence")
                .orElseGet(() -> departmentRepository.save(
                        new Department("Artificial Intelligence")
                ));

        departmentRepository
                .findByName("Robotics")
                .orElseGet(() -> departmentRepository.save(
                        new Department("Robotics")
                ));

        String encodedPassword = passwordEncoder.encode("password123");

        userRepository.findByEmail("admin@email.com")
                .orElseGet(() -> userRepository.save(
                        new User("admin@email.com", encodedPassword, Role.ADMIN)
                ));

        userRepository.findByEmail("user@user.com")
                .orElseGet(() -> {
                    User user1 = new User();
                    user1.setEmail("user@user.com");
                    user1.setPassword(encodedPassword);
                    user1.setRole(Role.USER);
                    user1.setDepartment(aiDepartment);
                    user1.setClearance(Clearance.HIGH);
                    user1.setUserAccessPolicy(
                            "Department::Artificial Intelligence && Clearance::HIGH"
                    );
                    return userRepository.save(user1);
                });


    }

}

