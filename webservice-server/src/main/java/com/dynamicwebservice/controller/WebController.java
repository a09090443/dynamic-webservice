package com.dynamicwebservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping({"/thymeleaf"})
    public String thymeleaf() {
        return "html/hello";
    }

    @GetMapping({"/jsp"})
    public String jsp() {
        return "jsp/hello";
    }
}
