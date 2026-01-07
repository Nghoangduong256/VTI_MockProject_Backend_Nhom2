package com.vti.springdatajpa.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserAvatarUpdateDTO {
    @NotBlank(message = "Avatar URL is required")
    private String avatar; // Base64 thuần (KHÔNG prefix) hoặc URL

}
