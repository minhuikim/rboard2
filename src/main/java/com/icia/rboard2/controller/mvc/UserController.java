package com.icia.rboard2.controller.mvc;

import java.security.*;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import com.icia.rboard2.dto.UserDto.*;
import com.icia.rboard2.service.mvc.*;
import com.icia.rboard2.util.editor.*;

@Controller
public class UserController {
	@Autowired
	private UserService service;
	
	@InitBinder
	public void init(WebDataBinder wdb) {
		wdb.registerCustomEditor(Date.class, "birthday", new DatePropertyEditor());
		wdb.registerCustomEditor(List.class, "authorities", new AuthorityPropertyEditor());
	}
	
	@PreAuthorize("isAnonymous()")
	@PostMapping("/user/join")
	public ModelAndView join(JoinRequestDto dto, RedirectAttributes ra) {
		service.join(dto);
		ra.addFlashAttribute("msg", "가입확인메일을 보냈습니다. 확인해주십시오");
		return new ModelAndView("redirect:/user/login");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping("/user/joinCheck")
	public ModelAndView joinCheck(@RequestParam String checkCode, RedirectAttributes ra) {
		if(service.joinCheck(checkCode)==false) {
			ra.addFlashAttribute("msg", "체크코드를 확인할 수 없습니다");
			return new ModelAndView("redirect:/");
		}
		ra.addFlashAttribute("msg", "체크코드 확인에 성공했습니다. 로그인해주세요");
		return new ModelAndView("redirect:/user/login");
	}
	
	@PreAuthorize("isAnonymous()")
	@PostMapping("/user/findId")
	public ModelAndView findId(String irum, String email, RedirectAttributes ra) {
		if(irum==null || email==null)
			ra.addFlashAttribute("msg", "이름과 이메일을 정확히 입력하세요");
		else {
			String username = service.findId(irum, email);
			String msg = username!=null? "회원님의 아이디 : " + username : "아이디를 찾지 못했습니다";
			ra.addFlashAttribute("msg", msg);
		}
		return new ModelAndView("redirect:/user/findId");
	}
	
	@PreAuthorize("isAnonymous()")
	@PostMapping("/user/findPwd")
	public ModelAndView resetPwd(String username, String email, RedirectAttributes ra) {
		if(username==null || email==null) {
			ra.addFlashAttribute("msg", "아이디와 이메일을 정확히 입력하세요");
			return new ModelAndView("redirect:/user/findPwd");
		} else {
			Boolean password = service.resetPwd(username, email);
			String msg = password!=null? "임시비밀번호를 " + email + "로 보냈습니다" : "비밀번호를 찾지 못했습니다";
			ra.addFlashAttribute("msg", msg);
			return new ModelAndView("redirect:/");
		}
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/user/pwdCheck")
	public ModelAndView pwdCheck(String password, HttpSession session, Principal principal, RedirectAttributes ra) {
		if(service.pwdCheck(password, principal.getName())==false) {
			ra.addFlashAttribute("msg", "잘못된 비밀번호입니다");
			return new ModelAndView("redirect:/user/pwdCheck");
		} 
		session.setAttribute("pwdCheck", "true");
		return new ModelAndView("redirect:/user/read");
	}

	@GetMapping("/user/read")						//로그인안했으면 null, 로그인 했으면 id저장
	public ModelAndView read(HttpSession session, Principal principal, RedirectAttributes ra) {
		if(session.getAttribute("pwdCheck")==null) {
			ra.addFlashAttribute("msg", "비밀번호를 확인하지 못했습니다");
			return new ModelAndView("redirect:/user/pwdCheck");
		}
		return new ModelAndView("main").addObject("viewname","user/read.jsp").addObject("user", service.read(principal.getName()));
	}
	
	//아이디 삭제시 로그아웃을 해야하기 때문에 authentication을 사용
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/user/resign")
	public String resign(SecurityContextLogoutHandler handler, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// 스프링 시큐리티에서 사용자 인증정보를 저장하는 객체 : Authentication 
		// authentication.getName()을 하면 비로그인일때 "anonymousUser"
		service.resign(authentication.getName());
		handler.logout(request, response, authentication);
		return "redirect:/";
	}
}
