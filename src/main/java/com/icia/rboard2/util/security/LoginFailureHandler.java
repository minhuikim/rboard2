package com.icia.rboard2.util.security;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.entity.*;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	// 로그인에 실패한다
	// 아이디가 없다 : InternalAuthenticationServiceException
	//			  인증서비스를 직접 구현해야 사용 가능
	// 비밀번호가 틀렸다 : BadCredentialsException
	// 계정 블록 : DisabledException
	@Autowired
	private UserDao dao;
	private RedirectStrategy rs = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = request.getParameter("username");
		HttpSession session = request.getSession(); //세션에 msg등 값을 넣으면 사라지지 않기 때문에 출력하자마자 사라지게 지워줘야한다.(인터셉터) 
		// Fruit fruit <-- Apple apple, Orange orange
		// if(fruit instanceof Apple)
		if(exception instanceof BadCredentialsException) {
			User user = dao.findById(username);
			// 아이디가 없는 경우
			if(user==null) {
				session.setAttribute("msg", "아이디를 찾을 수 없습니다");
			} else {
				// 비밀번호가 틀린 경우
				int loginFailureCnt = user.getLoginFailureCnt()+1;
				if(loginFailureCnt<5) {
					dao.update(User.builder().username(username).loginFailureCnt(loginFailureCnt).build());
					session.setAttribute("msg", loginFailureCnt+"회 로그인에 실패했습니다");
				} else {
					dao.update(User.builder().username(username).loginFailureCnt(loginFailureCnt).enabled(false).build());
					session.setAttribute("msg", "로그인에 5회 실패해 계정이 블록되었습니다");
				}
			}
		} else if(exception instanceof DisabledException) {
			session.setAttribute("msg", "블록된 계정입니다. 관리자에게 문의하세요");
		}
		rs.sendRedirect(request, response, "/user/login");
	}
}






