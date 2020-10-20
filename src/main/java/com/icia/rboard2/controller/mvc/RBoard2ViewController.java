package com.icia.rboard2.controller.mvc;

import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class RBoard2ViewController {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/board/write")
	public ModelAndView write() {
		return new ModelAndView("main").addObject("viewname", "board/write.jsp");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/user/join")
	public ModelAndView join() {
		return new ModelAndView("main").addObject("viewname", "user/join.jsp");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/user/findId")
	public ModelAndView findId() {
		return new ModelAndView("main").addObject("viewname", "user/findId.jsp");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/user/findPwd")
	public ModelAndView resetPwd() {
		return new ModelAndView("main").addObject("viewname", "user/resetPwd.jsp");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/user/login")
	public ModelAndView login() {
		return new ModelAndView("main").addObject("viewname", "user/login.jsp");
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/user/pwdCheck")
	public ModelAndView checkPwd() {
		return new ModelAndView("main").addObject("viewname","user/pwdCheck.jsp");
	}
}
