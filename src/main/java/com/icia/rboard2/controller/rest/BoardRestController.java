package com.icia.rboard2.controller.rest;

import java.io.*;
import java.net.URLEncoder;
import java.security.*;
import java.util.*;

import org.apache.commons.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import com.icia.rboard2.entity.*;
import com.icia.rboard2.service.rest.*;

@RestController
public class BoardRestController {
	@Autowired
	private BoardRestService service;
	@Value("d:/upload/attachment")
	private String attachmentFolder;
	
	// 글 추천 : 글쓴이 추가 -> 아이디, 글번호가 users_board에 없을 경우에만 추천 처리
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path="/board/good", method=RequestMethod.POST)
	public ResponseEntity<?> good(@RequestParam Integer bno, Principal principal) {
		//log.info("출력 확인 : {}", bno); -> 출력확인의 레벨을 조정해서 사용
		Integer goodCnt = service.good(bno, principal.getName());
		return ResponseEntity.ok(goodCnt);
	}
	
	// 글 추천 : 글쓴이 추가 -> 아이디, 글번호가 users_board에 없을 경우에만 비추 처리
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path="/board/bad", method=RequestMethod.POST)
	public ResponseEntity<?> bad(@RequestParam Integer bno, Principal principal) {
		Integer badCnt = service.bad(bno, principal.getName());
		return ResponseEntity.ok(badCnt);
	}
	
	// 댓글 작성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/comment/write")
	public ResponseEntity<?> writeComment(@ModelAttribute Comment comment, Principal principal) {
		comment.setWriter(principal.getName());
		List<Comment> list = service.writeComment(comment);
		return ResponseEntity.ok(list);
	}
	
	// 여기 수정 : 댓글 삭제 : Post				REST
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/comment/delete")
	public ResponseEntity<?> deleteComment(@RequestParam Integer cno, @RequestParam Integer bno, Principal principal) {
		List<Comment> list = service.deleteComment(cno, bno, principal.getName());
		return ResponseEntity.ok(list);
	}
	
	// ck사진 업로드 : MultipartFile -> JSON
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/ckImageUpload")
	public ResponseEntity<?> ckImageUpload(MultipartFile upload) {
		return ResponseEntity.ok(service.ckImageUpload(upload));
	}
	
	@GetMapping("/attachment/download")
	public ResponseEntity<?> download(Integer ano) throws UnsupportedEncodingException {
		// 응답 헤더에 Content_Dispostion을 inline을 주면 보기, attachment를 주면 다운로드
		Attachment a = service.readAttachment(ano);
		String originalFileName = a.getOriginalFileName();
		File saveFile = new File(attachmentFolder, a.getSaveFileName());
		HttpHeaders headers = new HttpHeaders();
		if(a.getIsImage()==true) {
			String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
			ext = ext.toUpperCase();
			if(ext.equals("JPG") || ext.equals("JPEG"))
				headers.setContentType(MediaType.IMAGE_JPEG);
			else if(ext.equals("PNG"))
				headers.setContentType(MediaType.IMAGE_PNG);
		} else {
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.add("Content-Disposition", "attachment;filename*=UTF-8''"+URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20"));
		}
		byte[] attachmentFile = null;
		try {
			attachmentFile = FileUtils.readFileToByteArray(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().headers(headers).body(attachmentFile);
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/attachment/delete")
	public ResponseEntity<?> deleteAttachment(Integer ano, Integer bno, Principal principal) {
		List<Attachment> attachments = service.deleteAttachment(ano, bno, principal.getName());
		return ResponseEntity.ok(attachments);
	}
}














