package com.icia.rboard2.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.icia.rboard2.exception.*;

@ControllerAdvice
public class RBoard2ControllerAdvice {
	
	@ExceptionHandler(IllegalJobException.class)
	public ModelAndView illegalJobExceptionHandler() {
		return new ModelAndView("main").addObject("viewname","system/error.jsp").addObject("msg", "비정상적인 작업을 요청해 거부되었습니다");
	}
	
	@ExceptionHandler(BoardNotFoundException.class)
	public ModelAndView boardNotFoundExceptionHandler(BoardNotFoundException e) {
		return new ModelAndView("main").addObject("viewname","system/error.jsp").addObject("msg", "선택하신 글이 존재하지 않습니다");
	}
	
	@ExceptionHandler(IllegalJobRestException.class)
	public ResponseEntity<?> illegalJobRestExceptionHandler() {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("비정상적인 작업을 요청해 거부되었습니다");
	}								//상태코드 : 503 - (글, 댓글, 첨부파일)을 작성자가 아닌 아이디가 삭제하려고 하는 경우
}
