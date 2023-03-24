package com.example.testproject.controllers;

import com.example.testproject.models.LoginProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginProcessor loginProcessor;

    public LoginController(LoginProcessor loginProcessor) {
        this.loginProcessor = loginProcessor;
    }

    @GetMapping("/login")
    public String login(@RequestParam String nameUser) {
        loginProcessor.setNameUser(nameUser);
        loginProcessor.login();
        return "Success";
    }
}
