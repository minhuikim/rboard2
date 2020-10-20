package com.icia.rboard2.controller.rest;

import java.security.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import com.icia.rboard2.dto.UserDto.*;
import com.icia.rboard2.service.rest.*;

@RestController
public class UserRestController {
	@Autowired
	private UserRestService service;
	
	@PreAuthorize("isAnonymous()")
	@GetMapping(path="/user/idCheck", produces="text/plain;charset=utf-8")
	public ResponseEntity<String> idCheck(@RequestParam String username) {
		if(service.idCheck(username)==true)
			return ResponseEntity.ok("사용가능한 아이디입니다");
		return ResponseEntity.status(HttpStatus.CONFLICT).body("사용중인 아이디입니다");
	}
	
	@PreAuthorize("isAnonymous()")
	@GetMapping(path="/user/emailCheck", produces="text/plain;charset=utf-8")
	public ResponseEntity<String> pwdCheck(@RequestParam String email) {
		if(service.emailCheck(email)==true)
			return ResponseEntity.ok("사용가능한 이메일입니다");
		return ResponseEntity.status(HttpStatus.CONFLICT).body("사용중인 이메일입니다");
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/user/update")
	public ResponseEntity<?> update(UpdateRequestDto dto, Principal principal) {
		dto.setUsername(principal.getName());
		System.out.println(dto);
		if(service.update(dto)==true)
			return ResponseEntity.ok(null);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	}

}
