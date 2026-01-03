package com.example.quantumspringboot;

import com.example.quantumspringboot.dto.DepartmentDTO;
import com.example.quantumspringboot.dto.PolicyRequest;
import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.DepartmentRepository;
import com.example.quantumspringboot.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- FIND DEPARTMENT ----------------

    @Test
    void findDepartment_success() throws EntityDoesNotExistException {
        String name = "IT";
        Department department = new Department(name);
        DepartmentDTO dto = new DepartmentDTO();

        when(departmentRepository.existsDepartmentByNameIgnoreCase(name))
                .thenReturn(true);
        when(departmentRepository.findDepartmentByName(name))
                .thenReturn(department);
        when(objectMapper.convertValue(department, DepartmentDTO.class))
                .thenReturn(dto);

        DepartmentDTO result = departmentService.findDepartment(name);

        assertNotNull(result);
        verify(departmentRepository).findDepartmentByName(name);
    }

    @Test
    void findDepartment_notFound() {
        String name = "HR";

        when(departmentRepository.existsDepartmentByNameIgnoreCase(name))
                .thenReturn(false);

        assertThrows(EntityDoesNotExistException.class,
                () -> departmentService.findDepartment(name));
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAllDepartment_success() {
        Department dep1 = new Department("IT");
        Department dep2 = new Department("HR");

        DepartmentDTO dto1 = new DepartmentDTO();
        DepartmentDTO dto2 = new DepartmentDTO();

        when(departmentRepository.findAll())
                .thenReturn(Arrays.asList(dep1, dep2));

        when(objectMapper.convertValue(any(Department.class), eq(DepartmentDTO.class)))
                .thenAnswer(invocation -> {
                    Department dep = invocation.getArgument(0);
                    if (dep == dep1) return dto1;
                    if (dep == dep2) return dto2;
                    return null;
                });

        List<DepartmentDTO> result = departmentService.findAllDepartment();

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));
    }

    // ---------------- CREATE ENCRYPTION POLICY ----------------

    @Test
    void createEncryptionPolicy_singleDepartment() {
        PolicyRequest request = new PolicyRequest();
        request.setDepartments(List.of("IT"));
        request.setClearance("HIGH");

        String policy = departmentService.createEncryptionPolicy(request);

        assertEquals("Department::IT && Clearance::HIGH", policy);
    }

    @Test
    void createEncryptionPolicy_twoDepartments() {
        PolicyRequest request = new PolicyRequest();
        request.setDepartments(List.of("IT", "HR"));
        request.setClearance("LOW");

        String policy = departmentService.createEncryptionPolicy(request);

        assertEquals("(Department::IT || Department::HR) && Clearance::LOW", policy);
    }

    @Test
    void createEncryptionPolicy_invalidArguments() {
        PolicyRequest request = new PolicyRequest();
        request.setDepartments(List.of("IT", "HR", "FINANCE"));
        request.setClearance("LOW");

        assertThrows(IllegalArgumentException.class,
                () -> departmentService.createEncryptionPolicy(request));
    }

    // ---------------- CREATE DEPARTMENT ----------------

    @Test
    void createDepartment_success() throws EntityDoesNotExistException {
        String name = "Finance";
        Department department = new Department(name);
        DepartmentDTO dto = new DepartmentDTO();

        when(departmentRepository.existsDepartmentByNameIgnoreCase(name))
                .thenReturn(false);

        when(objectMapper.convertValue(any(Department.class), eq(DepartmentDTO.class)))
                .thenReturn(dto);

        DepartmentDTO result = departmentService.createDepartment(name);

        assertNotNull(result);
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_alreadyExists() {
        String name = "IT";

        when(departmentRepository.existsDepartmentByNameIgnoreCase(name))
                .thenReturn(true);

        assertThrows(EntityDoesNotExistException.class,
                () -> departmentService.createDepartment(name));

        verify(departmentRepository, never()).save(any());
    }
}
