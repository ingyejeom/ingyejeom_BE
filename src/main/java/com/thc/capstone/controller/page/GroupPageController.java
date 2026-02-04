package com.thc.capstone.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/group")
@Controller
public class GroupPageController {
    @GetMapping("/{page}")
    public String page(@PathVariable String page){
        return "group/" + page;
    }
    @GetMapping("/{page}/{id}")
    public String page2(@PathVariable String page, @PathVariable String id) {
        return "group/" + page;
    }
}