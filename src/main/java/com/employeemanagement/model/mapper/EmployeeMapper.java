package com.employeemanagement.model.mapper;

import com.employeemanagement.document.Employee;
import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;

public interface EmployeeMapper {
    Employee toDocument(EmployeeRequestDto dto);

    EmployeeResponseDto toDto(Employee document);
}
