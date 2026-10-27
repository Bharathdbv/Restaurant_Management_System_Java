package com.rms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the home page route.
 * JS equivalent: app.get('/', ...) => res.sendFile('main.html')
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "main";
    }
}
