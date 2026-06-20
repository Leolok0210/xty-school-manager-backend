package com.xiaotiyun.school.manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health/check")
    public String health() {
        return "OK";
    }
}
