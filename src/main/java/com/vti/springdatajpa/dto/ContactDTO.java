package com.vti.springdatajpa.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ContactDTO {
    private UUID id;
    private String name;
    private String avatarUrl;
}
