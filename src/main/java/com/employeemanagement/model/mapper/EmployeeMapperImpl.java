package com.employeemanagement.model.mapper;

import com.employeemanagement.document.Employee;
import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class EmployeeMapperImpl implements EmployeeMapper {
    @Override
    public Employee toDocument(EmployeeRequestDto dto) {
        Assert.notNull(dto, "Dto object must not be null.");
        return Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .build();
    }

    @Override
    public EmployeeResponseDto toDto(Employee document) {
        Assert.notNull(document, "Document object must not be null.");
        return EmployeeResponseDto.builder()
                .id(document.getId())
                .firstName(document.getFirstName())
                .lastName(document.getLastName())
                .email(document.getEmail())
                .build();
    }
}
