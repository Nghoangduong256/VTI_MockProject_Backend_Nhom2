package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserManagerDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.service.UserManageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userManager")
@RequiredArgsConstructor
public class UserManagerController {
   private final UserManageService  userManageService;
   private final ModelMapper modelMapper ;

   @GetMapping("/all")
    public List<UserManagerDTO> getAllUsers() {
       List<User> users = userManageService.getAllUsers();
       return users.stream()
               .map(user -> modelMapper.map(user, UserManagerDTO.class))
               .toList();

   }

    @PutMapping("/lock/{id}")
    public void lockUser(@PathVariable Integer id) {
        userManageService.lockUser(id);
    }

    @PutMapping("/unlock/{id}")
    public void unlockUser(@PathVariable Integer id) {
        userManageService.unlockUser(id);
    }


}
