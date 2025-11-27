package com.isabelly.tessiebooks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/private")
    public String privateRoute() {
        return "Você está autenticado!";
    }
}
