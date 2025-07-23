package com.vision.vision_platform_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/")
    public String hello() {
        return "VSS Backend API is working!";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint working!";
    }

}
