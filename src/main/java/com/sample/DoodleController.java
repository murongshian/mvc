package com.sample;

import com.murongshian.core.annotation.Controller;
import com.murongshian.mvc.annotation.RequestMapping;
import com.murongshian.mvc.annotation.ResponseBody;

@Controller
@RequestMapping
public class DoodleController {
    @RequestMapping
    @ResponseBody
    public String hello() {
        return "你好，世界";
    }
}