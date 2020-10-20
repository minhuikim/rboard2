package com.icia.rboard2.controller.mvc;

import java.security.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.icia.rboard2.dto.*;
import com.icia.rboard2.entity.*;
import com.icia.rboard2.service.mvc.*;

@Controller
public class BoardController {
	@Autowired
	private BoardService service;
	
	@GetMapping({"/", "/board/list"})
	public ModelAndView list(@RequestParam(defaultValue="1") Integer pageno) {
		return new ModelAndView("main").addObject("viewname", "board/list.jsp").addObject("page", service.list(pageno));
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/write")
	public ModelAndView write(@ModelAttribute BoardWriteDto dto, Principal principal) {
		dto.setWriter(principal.getName());
		Integer bno = service.write(dto);
		return new ModelAndView("redirect:/board/read?bno=" + bno);
	}
	
	@GetMapping("/board/read")
	public ModelAndView read(@RequestParam Integer bno, Principal principal) {
		String username = (principal!=null)? principal.getName(): null;
		return new ModelAndView("main").addObject("viewname", "board/read.jsp").addObject("board", service.read(bno, username));
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/update")
	public ModelAndView update(@ModelAttribute Board board, Principal principal) {
		board.setWriter(principal.getName());
		service.update(board);
		return new ModelAndView("redirect:/board/read?bno=" + board.getBno());
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/delete")
	public ModelAndView delete(@RequestParam Integer bno, Principal principal) {
		service.delete(bno, principal.getName());
		return new ModelAndView("redirect:/");
	}
}