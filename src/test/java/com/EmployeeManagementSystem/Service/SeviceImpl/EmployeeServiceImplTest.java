package com.EmployeeManagementSystem.Service.SeviceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.EmployeeManagementSystem.DTO.EmployeeDTO;
import com.EmployeeManagementSystem.Entity.Employee;
import com.EmployeeManagementSystem.Entity.User;
import com.EmployeeManagementSystem.Exceptions.SomeThingWentWrong;
import com.EmployeeManagementSystem.Enum.Role;
import com.EmployeeManagementSystem.Mapper.MapperClassToDTO;
import com.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.EmployeeManagementSystem.Security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MapperClassToDTO mapperClassToDTO;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        
        // Setup @Value injection manually during Unit Testing
        ReflectionTestUtils.setField(employeeService, "checkEmployeeId", "You are not authorized to view employee %d");
    }

    @Test
    void testFindEmployeeById_Success() {
        Long employeeId = 1L;
        String email = "test@email.com";
        
        // Mock Security Context
        User securityUser = new User();
        securityUser.setEmail(email);
        securityUser.setRole(Role.EMPLOYEE);
        CustomUserDetails userDetails = new CustomUserDetails(securityUser);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getName()).thenReturn(email);

        // Mock DB Results
        Employee employee = new Employee();
        employee.setId(employeeId);
        
        when(employeeRepository.findByIdAndUserEmail(employeeId, email))
                .thenReturn(Optional.of(employee));
                
        EmployeeDTO mockDTO = new EmployeeDTO();
        mockDTO.setId(employeeId);
        when(mapperClassToDTO.entityToDTO(employee)).thenReturn(mockDTO);

        // Execute
        EmployeeDTO result = employeeService.findEmployeeById(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(employeeRepository, times(1)).findByIdAndUserEmail(employeeId, email);
    }

    @Test
    void testFindEmployeeById_UnauthorizedOrMissing() {
        Long employeeId = 2L;
        String email = "hacker@email.com";
        
        // Mock Security Context
        User securityUser = new User();
        securityUser.setEmail(email);
        securityUser.setRole(Role.EMPLOYEE);
        CustomUserDetails userDetails = new CustomUserDetails(securityUser);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getName()).thenReturn(email);

        // Mock Database resolving to NOTHING (Unauthenticated view or Missing ID)
        when(employeeRepository.findByIdAndUserEmail(employeeId, email))
                .thenReturn(Optional.empty());
                
        // Execute & Assert
        SomeThingWentWrong exception = assertThrows(SomeThingWentWrong.class, () -> {
            employeeService.findEmployeeById(employeeId);
        });
        
        assertEquals("You are not authorized to view employee 2", exception.getMessage());
        verify(employeeRepository, times(1)).findByIdAndUserEmail(employeeId, email);
        verify(mapperClassToDTO, never()).entityToDTO(any());
    }
}
