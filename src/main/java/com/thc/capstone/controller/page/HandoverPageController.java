package com.thc.capstone.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// 인수인계 관련 HTML 페이지를 보여주는 컨트롤러
@RequestMapping("/handover")
@Controller
public class HandoverPageController {

    // /handover/create, /handover/edit 등 페이지 요청 처리
    @GetMapping("/{page}")
    public String page(@PathVariable String page) {
        return "handover/" + page;
    }

    // /handover/edit/1, /handover/create/5 등 ID가 포함된 페이지 요청 처리
    @GetMapping("/{page}/{id}")
    public String pageWithId(@PathVariable String page, @PathVariable String id) {
        return "handover/" + page;
    }
}
