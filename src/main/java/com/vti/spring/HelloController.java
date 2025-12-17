package com.vti.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
//url: localhost:8080/hello
public class HelloController {


    @GetMapping
    public String hello() {
        return "Hello world";
    }
}
