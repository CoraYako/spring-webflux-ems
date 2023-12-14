package com.employeemanagement.service;

import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<EmployeeResponseDto> createEmployee(EmployeeRequestDto requestDto);

    Mono<EmployeeResponseDto> updateEmployee(String id, EmployeeRequestDto requestDto);

    Mono<EmployeeResponseDto> getEmployeeById(String id);

    Flux<EmployeeResponseDto> listEmployees();

    Mono<Void> deleteEmployeeById(String id);
}
