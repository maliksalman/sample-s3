package com.smalik.s3sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping("/")
    public String swaggerUI() {
        return "redirect:swagger-ui.html";
    }
}
