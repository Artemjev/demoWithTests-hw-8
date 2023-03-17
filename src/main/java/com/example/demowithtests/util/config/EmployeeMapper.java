package com.example.demowithtests.util.config;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.dto.EmployeeReadDto;
import com.example.demowithtests.dto.employee.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {


    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

//    @Mapping(source = "firstName", target = "name")
//    @Mapping(source = "address.city", target = "city")
    EmployeeDto employeeToEmployeeDTO(Employee employee);
    EmployeeReadDto employeeToEmployeeReadDTO(Employee employee);

    Employee employeeDTOToEmployee(EmployeeDto employeeDto);
}