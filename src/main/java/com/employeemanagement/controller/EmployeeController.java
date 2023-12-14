package com.employeemanagement.controller;

import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import com.employeemanagement.service.EmployeeService;
import com.employeemanagement.utils.EmployeeManagementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = EmployeeManagementUtils.BASE_URL)
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Mono<EmployeeResponseDto>> createEmployee(@RequestBody EmployeeRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(requestDto));
    }

    @PatchMapping(value = EmployeeManagementUtils.URI_VARIABLE)
    public ResponseEntity<Mono<EmployeeResponseDto>> updateEmployee
            (@PathVariable String id, @RequestBody EmployeeRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.updateEmployee(id, requestDto));
    }

    @GetMapping(value = EmployeeManagementUtils.URI_VARIABLE)
    public ResponseEntity<Mono<EmployeeResponseDto>> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<Flux<EmployeeResponseDto>> listEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.listEmployees());
    }

    @DeleteMapping(value = EmployeeManagementUtils.URI_VARIABLE)
    public ResponseEntity<Mono<Void>> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployeeById(id).block();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
