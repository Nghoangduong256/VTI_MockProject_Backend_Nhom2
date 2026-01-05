package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class UserAvatarUpdateDTO {
    private String avatar; // Base64 thuần (KHÔNG prefix)

}
