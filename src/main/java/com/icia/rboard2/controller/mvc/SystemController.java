package com.icia.rboard2.controller.mvc;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class SystemController {
	@GetMapping("/system/403")
	public ModelAndView e403() {
		return new ModelAndView("main").addObject("msg", "잘못된 접근입니다(에러코드: 403, Forbidden)")
			.addObject("viewname","system/error.jsp");
	}
}
