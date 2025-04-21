package com.yarovyi.flowmeter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {


    @GetMapping
    public String getHome() {
        return "pub/index";
    }


    @GetMapping("/profile")
    public String getProfilePage() {
        return "auth/profile";
    }


    @GetMapping("/flow")
    public String getFlowManagePage() {
        return "auth/flowmeter-panel";
    }


}
