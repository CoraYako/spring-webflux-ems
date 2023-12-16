package com.employeemanagement.model.dto;

public record EmployeeResponseDto(
        String id,
        String firstName,
        String lastName,
        String email
) {
    public static EmployeeResponseDtoBuilder builder() {
        return new EmployeeResponseDtoBuilder();
    }

    public static class EmployeeResponseDtoBuilder {
        private String id;
        private String firstName;
        private String lastName;
        private String email;

        public EmployeeResponseDtoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EmployeeResponseDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public EmployeeResponseDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public EmployeeResponseDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EmployeeResponseDto build() {
            return new EmployeeResponseDto(id, firstName, lastName, email);
        }
    }
}
