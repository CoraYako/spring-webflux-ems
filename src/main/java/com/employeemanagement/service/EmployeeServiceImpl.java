package com.employeemanagement.service;

import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import com.employeemanagement.model.mapper.EmployeeMapper;
import com.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Mono<EmployeeResponseDto> createEmployee(EmployeeRequestDto requestDto) {
        Assert.notNull(requestDto, "Dto request to create Employee must not be null.");
        return employeeRepository.insert(employeeMapper.toDocument(requestDto)).map(employeeMapper::toDto);
    }

    @Override
    public Mono<EmployeeResponseDto> updateEmployee(String id, EmployeeRequestDto requestDto) {
        Assert.notNull(id, "Employee ID must not be null.");
        return employeeRepository.findById(id).flatMap(employee -> {
                    employee.setFirstName(requestDto.firstName());
                    employee.setLastName(requestDto.lastName());
                    employee.setEmail(requestDto.email());
                    return employeeRepository.save(employee);
                })
                .map(employeeMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException(STR."Employee not found for ID \{id}")));
    }

    @Override
    public Mono<EmployeeResponseDto> getEmployeeById(String id) {
        Assert.notNull(id, "Employee ID must not be null.");
        return employeeRepository.findById(id).map(employeeMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException(STR."Employee not found for ID \{id}")));
    }

    @Override
    public Flux<EmployeeResponseDto> listEmployees() {
        return employeeRepository.findAll().map(employeeMapper::toDto).switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Void> deleteEmployeeById(String id) {
        Assert.notNull(id, "Employee ID must not be null.");
        return employeeRepository.deleteById(id);
    }
}
