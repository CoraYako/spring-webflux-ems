package com.employeemanagement.integration;

import com.employeemanagement.document.Employee;
import com.employeemanagement.model.HttpErrorCode;
import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import com.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.employeemanagement.utils.EmployeeManagementUtils.BASE_URL;
import static com.employeemanagement.utils.EmployeeManagementUtils.URI_VARIABLE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT extends AbstractContainerBaseTest {
    private static final String URL_TEMPLATE = BASE_URL + URI_VARIABLE;
    private final EmployeeRepository employeeRepository;
    private final WebTestClient webTestClient;

    @Autowired
    public EmployeeControllerIT(EmployeeRepository employeeRepository, WebTestClient webTestClient) {
        this.employeeRepository = employeeRepository;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll().subscribe();
    }

    @DisplayName(value = "JUnit Test for create an Employee successfully")
    @Test
    public void givenRequestDto_whenCreateEmployee_thenEmployeeCreatedAndStatusCreatedIsReturned() {
        // given
        final EmployeeRequestDto requestDto = EmployeeRequestDto.builder()
                .firstName("Test Name")
                .lastName("Test Last Name")
                .email("test@testemail.com")
                .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), EmployeeRequestDto.class)
                .exchange();

        // then
        response.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo(requestDto.firstName())
                .jsonPath("$.lastName").isEqualTo(requestDto.lastName())
                .jsonPath("$.email").isEqualTo(requestDto.email());
    }

    @DisplayName(value = "JUnit Test for get and Employee by ID")
    @Test
    public void givenEmployeeId_whenGetEmployee_thenEmployeeAndStatusOkIsReturned() {
        // ...let's first create and save an Employee
        Employee employee = Employee.builder()
                .firstName("Test Name")
                .lastName("Test Last Name")
                .email("test@testemail.com")
                .build();
        employee = employeeRepository.insert(employee).block();
        Objects.requireNonNull(employee);

        // given
        final String employeeId = employee.getId();

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(employeeId)
                .jsonPath("$.firstName").isEqualTo("Test Name")
                .jsonPath("$.lastName").isEqualTo("Test Last Name")
                .jsonPath("$.email").isEqualTo("test@testemail.com");
    }

    @DisplayName(value = "JUnit Test for get an Employee by ID but is not present in the database")
    @Test
    public void givenEmployeeId_whenGetEmployee_thenStatusNotFoundIsReturned() {
        // given
        final String employeeId = "1234567890";

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(STR."Employee not found for ID \{employeeId}")
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.errorCode").isEqualTo(HttpErrorCode.RESOURCE_NOT_FOUND.toString());
    }

    @DisplayName(value = "JUnit Test for list all Employees")
    @Test
    public void givenRequest_whenListEmployees_thenAllEmployeesFromDBWithStatusOkIsReturned() {
        // given

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(BASE_URL).exchange();

        // then
        response.expectStatus().isOk()
                .expectBodyList(EmployeeResponseDto.class)
                .consumeWith(System.out::println);
    }

    @DisplayName(value = "JUnit Test for update an Employee successfully")
    @Test
    public void givenEmployeeIdAndUpdateRequest_whenUpdateEmployee_thenUpdatedEmployeeWithStatusOkIsReturned() {
        // ...let's first create and save an Employee
        Employee employee = Employee.builder()
                .firstName("Test Name")
                .lastName("Test Last Name")
                .email("test@testemail.com")
                .build();
        employee = employeeRepository.insert(employee).block();
        Objects.requireNonNull(employee);

        // given
        final String employeeId = employee.getId();
        final EmployeeRequestDto updateRequest = EmployeeRequestDto.builder()
                .firstName("New Name")
                .lastName("New Last Name")
                .email("new@email.com")
                .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.patch().uri(URL_TEMPLATE, employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), EmployeeRequestDto.class)
                .exchange();

        // then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(employeeId)
                .jsonPath("$.firstName").isEqualTo(updateRequest.firstName())
                .jsonPath("$.lastName").isEqualTo(updateRequest.lastName())
                .jsonPath("$.email").isEqualTo(updateRequest.email());
    }

    @DisplayName(value = "JUnit Test for update an Employee but the Employee to update is not present in the database")
    @Test
    public void givenEmployeeIdAndUpdateRequest_whenGetEmployeeToUpdate_thenStatusNotFoundIsReturned() {
        // given
        final String employeeId = "1234567890";
        final EmployeeRequestDto updateRequest = EmployeeRequestDto.builder()
                .firstName("New Name")
                .lastName("New Last Name")
                .email("new@email.com")
                .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.patch().uri(URL_TEMPLATE, employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), EmployeeRequestDto.class)
                .exchange();

        // then
        response.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(STR."Employee not found for ID \{employeeId}")
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.errorCode").isEqualTo(HttpErrorCode.RESOURCE_NOT_FOUND.toString());
    }

    @DisplayName(value = "JUnit Test for delete an Employee successfully")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenStatusNoContentIsReturned() {
        // ...let's first create and save an Employee
        Employee employee = Employee.builder()
                .firstName("Test Name")
                .lastName("Test Last Name")
                .email("test@testemail.com")
                .build();
        employee = employeeRepository.insert(employee).block();
        Objects.requireNonNull(employee);

        // given
        final String employeeId = employee.getId();

        // when
        WebTestClient.ResponseSpec response = webTestClient.delete().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
        assertThat(employeeRepository.existsById(employeeId).block()).isFalse();
    }
}