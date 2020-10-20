package com.icia.rboard2.util.interceptor;

import javax.servlet.http.*;

import org.springframework.web.servlet.handler.*;

public class LoginRedirectMessageInterceptor extends HandlerInterceptorAdapter { //Interceptor를 직접 사용하지 않고 중간단계인 adapter를 가져온다.
	// preHandle, postHandle
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		if(session.getAttribute("msg")!=null) {
			System.out.println("### 인터셉터 동작 ###");
			request.setAttribute("msg", session.getAttribute("msg"));
			session.removeAttribute("msg");
		}
		return super.preHandle(request, response, handler);
	}
	

}
