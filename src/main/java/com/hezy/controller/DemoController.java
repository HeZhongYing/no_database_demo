package com.hezy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:24
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping
    public String demo() {
        return "Hello World";
    }
}
