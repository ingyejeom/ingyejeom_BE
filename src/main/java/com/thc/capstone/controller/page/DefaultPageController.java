package com.thc.capstone.controller.page;

import com.thc.capstone.security.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

@RequestMapping("")
@Controller
public class DefaultPageController {
/*

    @RequestMapping("/index")
    public String index(){
        System.out.println("DefaultPageController.index()");
        return "index";
    }
*/
    @GetMapping({"/", "/index"}) // 루트 경로로 들어오면
    public String index(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return "redirect:/user/login"; // 로그인 안 했으면 로그인 창으로
        }
        return "redirect:/space/list"; // 로그인 했으면 스페이스 목록으로
    }

    @RequestMapping("/admin")
    public String admin(){
        return "admin";
    }

}