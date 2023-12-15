package com.employeemanagement;

import com.employeemanagement.controller.EmployeeController;
import com.employeemanagement.model.HttpErrorCode;
import com.employeemanagement.model.dto.EmployeeRequestDto;
import com.employeemanagement.model.dto.EmployeeResponseDto;
import com.employeemanagement.service.EmployeeService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.employeemanagement.utils.EmployeeManagementUtils.BASE_URL;
import static com.employeemanagement.utils.EmployeeManagementUtils.URI_VARIABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {
    private final String URL_TEMPLATE = BASE_URL + URI_VARIABLE;

    private final WebTestClient webTestClient;
    @MockBean
    private final EmployeeService employeeService;
    private final ObjectId objectId = new ObjectId();

    @Autowired
    public EmployeeControllerTest(WebTestClient webTestClient, EmployeeService employeeService) {
        this.webTestClient = webTestClient;
        this.employeeService = employeeService;
    }

    @DisplayName(value = "JUnit Test for create an Employee successfully")
    @Test
    public void givenRequestObject_whenCreateEmployee_thenEmployeeCreatedAndStatusCreatedIsReturned() {
        // given
        final EmployeeRequestDto requestDto = EmployeeRequestDto.builder()
                .firstName("Héctor")
                .lastName("Cortez")
                .email("hc@email.com")
                .build();
        given(employeeService.createEmployee(any(EmployeeRequestDto.class)))
                .willReturn(Mono.just(EmployeeResponseDto.builder()
                        .id(objectId.toHexString())
                        .firstName("Héctor")
                        .lastName("Cortez")
                        .email("hc@email.com")
                        .build())
                );

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
        // given
        final String employeeId = objectId.toHexString();
        given(employeeService.getEmployeeById(anyString()))
                .willReturn(Mono.just(EmployeeResponseDto.builder()
                        .id(employeeId)
                        .firstName("Héctor")
                        .lastName("Cortez")
                        .email("hc@email.com")
                        .build()));

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(employeeId)
                .jsonPath("$.firstName").isEqualTo("Héctor")
                .jsonPath("$.lastName").isEqualTo("Cortez")
                .jsonPath("$.email").isEqualTo("hc@email.com");
    }

    @DisplayName(value = "JUnit Test for get and Employee by ID but is not present in the database")
    @Test
    public void givenEmployeeId_whenGetEmployee_thenStatusNotFoundIsReturned() {
        // given
        final String employeeId = objectId.toHexString();
        given(employeeService.getEmployeeById(anyString()))
                .willThrow(new RuntimeException(STR."Employee not found for ID \{employeeId}"));

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
        List<EmployeeResponseDto> mockedList = List.of(mock(EmployeeResponseDto.class), mock(EmployeeResponseDto.class));
        given(employeeService.listEmployees()).willReturn(Flux.fromIterable(mockedList));

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(BASE_URL).exchange();

        // then
        response.expectStatus().isOk()
                .expectBodyList(EmployeeResponseDto.class)
                .consumeWith(System.out::println)
                .hasSize(2);
    }

    @DisplayName(value = "JUnit Test for update an Employee successfully")
    @Test
    public void givenEmployeeIdAndUpdateRequest_whenUpdateEmployee_thenUpdatedEmployeeWithStatusOkIsReturned() {
        // given
        final String employeeId = objectId.toHexString();
        final EmployeeRequestDto updateRequest = EmployeeRequestDto.builder()
                .firstName("New Name")
                .lastName("New Last Name")
                .email("new@email.com")
                .build();
        given(employeeService.updateEmployee(anyString(), any(EmployeeRequestDto.class)))
                .willReturn(Mono.just(EmployeeResponseDto.builder()
                        .id(employeeId)
                        .firstName(updateRequest.firstName())
                        .lastName(updateRequest.lastName())
                        .email(updateRequest.email())
                        .build()));

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
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo(updateRequest.firstName())
                .jsonPath("$.lastName").isEqualTo(updateRequest.lastName())
                .jsonPath("$.email").isEqualTo(updateRequest.email());
    }

    @DisplayName(value = "JUnit Test for get and Employee by ID but is not present in the database")
    @Test
    public void givenEmployeeIdAndUpdateRequest_whenGetEmployeeToUpdate_thenStatusNotFoundIsReturned() {
        // given
        final String employeeId = objectId.toHexString();
        final EmployeeRequestDto updateRequest = EmployeeRequestDto.builder()
                .firstName("New Name")
                .lastName("New Last Name")
                .email("new@email.com")
                .build();
        given(employeeService.updateEmployee(anyString(), any(EmployeeRequestDto.class)))
                .willThrow(new RuntimeException(STR."Employee not found for ID \{employeeId}"));

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
        // given
        final String employeeId = objectId.toHexString();
        given(employeeService.deleteEmployeeById(anyString())).willReturn(Mono.empty());

        // when
        WebTestClient.ResponseSpec response = webTestClient.delete().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @DisplayName(value = "JUnit Test for delete an Employee but the Employee is not present in the database")
    @Test
    public void givenEmployeeId_whenGetEmployeeToDelete_thenStatusNotFoundIsReturned() {
        // given
        final String employeeId = objectId.toHexString();
        given(employeeService.deleteEmployeeById(anyString()))
                .willThrow(new RuntimeException(STR."Employee not found for ID \{employeeId}"));

        // when
        WebTestClient.ResponseSpec response = webTestClient.delete().uri(URL_TEMPLATE, employeeId).exchange();

        // then
        response.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(STR."Employee not found for ID \{employeeId}")
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.errorCode").isEqualTo(HttpErrorCode.RESOURCE_NOT_FOUND.toString());
    }
}
