package com.icia.rboard2.util.security;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.*;
import org.springframework.stereotype.*;

import com.icia.rboard2.dao.*;
import com.icia.rboard2.entity.*;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Autowired
	private UserDao userDao;
	
	// 사용자가 가려던 목적지를 저장하는 객체
	private RequestCache cache = new HttpSessionRequestCache();
	// 리다이렉트 해주는 객체
	private RedirectStrategy rs = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 로그인 성공하면 실패 횟수 초기화, 로그인 횟수 증가
		User user = User.builder().username(authentication.getName()).loginFailureCnt(0).loginCnt(1).build();
		userDao.update(user);
		
		/* 여기서 기타 로그인 메시지를 처리하면 된다
		if(memoDao.isUnreadMemoExist(username)==true)
			session.setAttribute("memoMsg", "새로운 메모가 있습니다");
		*/
		
		// 이동. 특별한 목적지가 없으면 /로 이동
		SavedRequest savedRequest = cache.getRequest(request, response);
		if(savedRequest!=null)
			rs.sendRedirect(request, response, savedRequest.getRedirectUrl());
		else
			rs.sendRedirect(request, response, "/");
	}
}






