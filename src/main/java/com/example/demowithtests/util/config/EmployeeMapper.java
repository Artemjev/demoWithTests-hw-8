package com.example.demowithtests.util.config;


import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.dto.employee.EmployeeCreateDto;
import com.example.demowithtests.dto.employee.EmployeeReadDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeReadDto employeeToEmployeeReadDTO(Employee employee);
    //    EmployeeCreateDto employeeToEmployeeCreateDTO(Employee employee);
    //    EmployeePatchDto employeeToEmployeePatchDTO(Employee employee);
    Employee employeeCreateDtoToEmployee(EmployeeCreateDto employeeCreateDto);
    //    Employee employeeReadDTOToEmployee(EmployeeCreateDto employeeReadDto);
    //  Employee employeePutDtoToEmployee(EmployeeCreateDto employeePutDto);
}