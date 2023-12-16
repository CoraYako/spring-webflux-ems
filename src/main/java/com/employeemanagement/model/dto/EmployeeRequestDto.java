package com.employeemanagement.model.dto;

public record EmployeeRequestDto(
        String firstName,
        String lastName,
        String email
) {
    public static EmployeeRequestDtoBuilder builder() {
        return new EmployeeRequestDtoBuilder();
    }

    public static class EmployeeRequestDtoBuilder {
        private String firstName;
        private String lastName;
        private String email;

        public EmployeeRequestDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public EmployeeRequestDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public EmployeeRequestDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EmployeeRequestDto build() {
            return new EmployeeRequestDto(firstName, lastName, email);
        }
    }
}
